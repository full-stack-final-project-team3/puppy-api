package com.yp.puppy.api.service.community;

import com.yp.puppy.api.dto.BoardResponseDto;
import com.yp.puppy.api.dto.request.community.BoardSaveDto;
import com.yp.puppy.api.dto.response.community.BoardDetailResponseDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardImg;
import com.yp.puppy.api.entity.community.BoardView;
import com.yp.puppy.api.entity.community.Keyword;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.community.*;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardViewRepository boardViewRepository;
    private final LikeRepository likeRepository; // 좋아요 레포지토리 추가
    private final BoardImgRepository boardImgRepository;
    private final KeywordRepository keywordRepository;

    public List<BoardResponseDto> getBoardsWithLikeCounts(String sort, int page, int limit) {
        PageRequest pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, sort));
        Page<Board> boardPage = boardRepository.findAll(pageable);

        Set<Long> processedIds = new HashSet<>(); // 이미 처리된 ID를 추적하기 위한 Set

        return boardPage.getContent().stream()
                .filter(board -> processedIds.add(board.getId())) // 중복된 ID 걸러내기
                .map(board -> {
                    BoardResponseDto dto = convertToBoardResponseDto(board);
                    long likeCount = likeRepository.countByBoardId(board.getId());
                    dto.setLikeCount((int) likeCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<BoardResponseDto> getBoards(String sort) {
        List<Board> boards = boardRepository.findBoards(sort);
        return boards.stream()
                .map(this::convertToBoardResponseDto)
                .collect(Collectors.toList());
    }

    // 게시글 저장 메서드
    public BoardResponseDto saveBoard(BoardSaveDto dto, List<MultipartFile> files) {
        try {
            User user = userRepository.findById(dto.getUser().getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            Keyword keyword = null;
            if (dto.getKeyword() != null && dto.getKeyword().getId() != 0) {
                keyword = keywordRepository.findById(dto.getKeyword().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Keyword not found"));
            }

            Board board = Board.builder()
                    .boardTitle(dto.getBoardTitle())
                    .boardContent(dto.getBoardContent())
                    .user(user)
                    .keyword(keyword)
                    .boardCreatedAt(LocalDateTime.now())
                    .images(new ArrayList<>())
                    .build();

            Board savedBoard = boardRepository.save(board);

            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    BoardImg image = saveImage(file, savedBoard);
                    savedBoard.getImages().add(image);
                }
                savedBoard = boardRepository.save(savedBoard);
            }

            return convertToBoardResponseDto(savedBoard);
        } catch (Exception e) {
            log.error("Error saving board: ", e);
            throw new RuntimeException("Failed to save board", e);
        }
    }

    // 이미지 파일 저장 및 URL 생성 메서드
    private BoardImg saveImage(MultipartFile file, Board board) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);

            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            // 이미지 파일의 URL 생성
            String imgUrl = "/uploads/" + fileName;

            return BoardImg.builder()
                    .imgUrl(imgUrl)
                    .board(board)
                    .build();
        } catch (IOException e) {
            log.error("Error saving image: ", e);
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public BoardResponseDto getBoardById(long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        return convertToBoardResponseDto(board);
    }

    public BoardDetailResponseDto getBoardDetailById(long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        return convertToBoardDetailResponseDto(board);
    }

    public void deleteBoard(Long boardId, String userId, boolean isAdmin) {
        log.info("Attempting to delete board with id: {} by user: {}", boardId, userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));

        // 사용자 권한 체크
        if (!isAdmin && !board.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You don't have permission to delete this board");
        }

        boardViewRepository.deleteByBoard(board);

        // 조회수와 상관없이 게시글 삭제
        boardRepository.delete(board);
        log.info("Board with id: {} deleted successfully", boardId);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardSaveDto dto, List<MultipartFile> files, List<String> imagesToDelete) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));

        if (!board.getUser().getId().equals(dto.getUser().getId())) {
            throw new IllegalStateException("You don't have permission to update this board");
        }

        board.setBoardTitle(dto.getBoardTitle());
        board.setBoardContent(dto.getBoardContent());
        board.setBoardUpdatedAt(LocalDateTime.now());

        // 키워드 수정
        if (dto.getKeyword() != null) {
            Keyword keyword = keywordRepository.findById(dto.getKeyword().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Keyword not found"));
            board.setKeyword(keyword);
        } else {
            board.setKeyword(null);
        }

        // 이미지 삭제 로직
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            board.getImages().removeIf(img -> imagesToDelete.contains(img.getImgUrl()));
        }

        // 새 이미지 추가
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                BoardImg image = saveImage(file, board);
                board.getImages().add(image);
            }
        }

        Board updatedBoard = boardRepository.save(board);
        return convertToBoardResponseDto(updatedBoard);
    }


    public BoardResponseDto convertToBoardResponseDto(Board board) {
        List<String> imageUrls = board.getImages().stream()
                .filter(img -> img.getBoardReply() == null && img.getBoardSubReply() == null)
                .map(BoardImg::getImgUrl)
                .collect(Collectors.toList());

        BoardResponseDto.KeywordDTO keywordDTO = null;
        if (board.getKeyword() != null) {
            keywordDTO = new BoardResponseDto.KeywordDTO(
                    board.getKeyword().getId(),
                    board.getKeyword().getName()
            );
        }

        return new BoardResponseDto(
                board.getId(),
                board.getBoardTitle(),
                board.getBoardContent(),
                imageUrls,
                board.getBoardCreatedAt(),
                board.getBoardUpdatedAt(),
                board.getViewCount(),
                board.getIsClean(),
                new BoardResponseDto.UserDTO(
                        board.getUser().getId(),
                        board.getUser().getNickname(),
                        board.getUser().getProfileUrl(),
                        board.getUser().getEmail()
                ),
                board.getReplies() != null ? board.getReplies().size() : 0,
                board.getLikes() != null ? board.getLikes().size() : 0,
                keywordDTO
        );
    }


    public BoardDetailResponseDto convertToBoardDetailResponseDto(Board board) {
        log.info("🌈 게시글 ID {} 변환 중...", board.getId());

        // 게시글 이미지 처리 (연관된 댓글이나 대댓글이 없는 경우에만)
        List<String> boardImageUrls = board.getImages().stream()
                .filter(image -> image.getBoardReply() == null && image.getBoardSubReply() == null)
                .map(BoardImg::getImgUrl)
                .collect(Collectors.toList());
        log.info("🌈 게시글 ID {}: 게시글 이미지 리스트: {}", board.getId(), boardImageUrls);

        List<BoardDetailResponseDto.ReplyDTO> replyDTOs = board.getReplies().stream()
                .map(reply -> {
                    log.info("🌈 댓글 ID {} 처리 중...", reply.getId());

                    // 댓글 이미지 처리 수정
                    String replyImageUrl = board.getImages().stream()
                            .filter(image -> image.getBoardReply() != null
                                    && image.getBoardReply().getId().equals(reply.getId())
                                    && image.getBoardSubReply() == null)
                            .findFirst()
                            .map(BoardImg::getImgUrl)
                            .orElse(null);

                    if (replyImageUrl != null) {
                        log.info("🌈 댓글 ID {}: 댓글 이미지 URL: {}", reply.getId(), replyImageUrl);
                    } else {
                        log.info("🌈 댓글 ID {}: 댓글 이미지 없음", reply.getId());
                    }

                    // 대댓글 처리
                    List<BoardDetailResponseDto.SubReplyDTO> subReplyDTOs = reply.getSubReplies().stream()
                            .map(subReply -> {
                                log.info("🌈 대댓글 ID {} 처리 중...", subReply.getId());

                                // 대댓글 이미지 처리
                                String subReplyImageUrl = null;
                                if (subReply.getImage() != null && subReply.getImage().getBoardSubReply() != null) {
                                    subReplyImageUrl = subReply.getImage().getImgUrl();
                                    log.info("🌈 대댓글 ID {}: 대댓글 이미지 URL: {}", subReply.getId(), subReplyImageUrl);

                                    // 이미지 필수 조건 확인
                                    if (subReplyImageUrl != null && subReply.getId() != null && reply.getId() != null) {
                                        log.info("🌈 대댓글 ID {}: 이미지가 올바르게 설정됨", subReply.getId());
                                    } else {
                                        log.error("🌈 대댓글 ID {}: 이미지 설정 오류 - 댓글 ID 또는 게시글 ID와 충돌", subReply.getId());
                                    }
                                } else {
                                    log.info("🌈 대댓글 ID {}: 대댓글 이미지 없음", subReply.getId());
                                }

                                return new BoardDetailResponseDto.SubReplyDTO(
                                        subReply.getId(),
                                        subReply.getSubReplyContent(),
                                        subReply.getSubReplyCreatedAt(),
                                        new BoardDetailResponseDto.UserDTO(
                                                subReply.getUser().getId(),
                                                subReply.getUser().getNickname(),
                                                subReply.getUser().getProfileUrl(),
                                                subReply.getUser().getEmail()
                                        ),
                                        subReplyImageUrl  // 대댓글 이미지 URL
                                );
                            })
                            .collect(Collectors.toList());

                    log.info("🌈 댓글 ID {}: {}개의 대댓글 처리 완료", reply.getId(), subReplyDTOs.size());

                    return new BoardDetailResponseDto.ReplyDTO(
                            reply.getId(),
                            reply.getReplyContent(),
                            reply.getReplyCreatedAt(),
                            new BoardDetailResponseDto.UserDTO(
                                    reply.getUser().getId(),
                                    reply.getUser().getNickname(),
                                    reply.getUser().getProfileUrl(),
                                    reply.getUser().getEmail()
                            ),
                            replyImageUrl,  // 수정된 댓글 이미지 URL
                            subReplyDTOs
                    );
                })
                .collect(Collectors.toList());

        log.info("🌈 게시글 ID {}: 총 {}개의 댓글 처리 완료", board.getId(), replyDTOs.size());

        return new BoardDetailResponseDto(
                board.getId(),
                board.getBoardTitle(),
                board.getBoardContent(),
                boardImageUrls,  // 게시글 이미지 URL 리스트
                board.getBoardCreatedAt(),
                board.getBoardUpdatedAt(),
                board.getViewCount(),
                board.getIsClean(),
                new BoardDetailResponseDto.UserDTO(
                        board.getUser().getId(),
                        board.getUser().getNickname(),
                        board.getUser().getProfileUrl(),
                        board.getUser().getEmail()
                ),
                replyDTOs
        );
    }

    //조회수
    public BoardDetailResponseDto getBoardDetailWithViewCount(Long id, String userId) {
        // ID가 null이거나 0보다 작으면 그냥 조회만 하고 넘어갑니다.
        if (id == null || id <= 0) {
            // ID가 유효하지 않은 경우에도 게시글 조회
            throw new IllegalArgumentException("ID must not be null or less than or equal to zero");
        }

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        // 비회원인 경우 userId는 null이므로, 조회수 증가 로직은 실행하지 않음
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            // 자신의 글인지 확인
            boolean isOwnPost = board.getUser().getId().equals(userId);

            if (!isOwnPost) {  // 자신의 글이 아닐 경우에만 조회수 로직 실행
                LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

                BoardView boardView = boardViewRepository.findByUserAndBoard(user, board)
                        .orElse(null);

                if (boardView == null || boardView.getLastViewedAt().isBefore(oneDayAgo)) {
                    board.setViewCount(board.getViewCount() + 1);
                    boardRepository.save(board);

                    if (boardView == null) {
                        boardView = new BoardView();
                        boardView.setUser(user);
                        boardView.setBoard(board);
                    }
                    boardView.setLastViewedAt(LocalDateTime.now());
                    boardViewRepository.save(boardView);
                }
            }
        }

        return convertToBoardDetailResponseDto(board);
    }
    //
    public List<BoardResponseDto> searchBoards(String keyword) {
        System.out.println("🐶 검색 키워드: " + keyword);

        // 모든 결과를 검색
        List<Board> searchResults = boardRepository.searchByTitleContaining(keyword);

        System.out.println("검색 결과 수: " + searchResults.size());

        if (!searchResults.isEmpty()) {
            return searchResults.stream()
                    .map(this::convertToBoardResponseDto)
                    .collect(Collectors.toList());
        } else {
            // 결과가 없을 때 처리
            return Collections.emptyList();
        }
    }
    //
    @Transactional
    public void deleteImage(Long boardId, String imageUrl, String userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));

        // 게시글 작성자 확인
        if (!board.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You don't have permission to delete this image");
        }

        // 이미지 삭제
        List<BoardImg> images = board.getImages();
        BoardImg imageToDelete = images.stream()
                .filter(img -> img.getImgUrl().equals(imageUrl))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        // 이미지 파일 시스템에서 삭제
        try {
            Path path = Paths.get(uploadDir, imageToDelete.getImgUrl().replace("/uploads/", ""));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Error deleting image file: ", e);
            throw new RuntimeException("Failed to delete image file", e);
        }

        // 이미지 리스트에서 제거
        images.remove(imageToDelete);
        boardRepository.save(board);  // 변경 사항 저장

        // 데이터베이스에서 이미지 경로 삭제
        boardImgRepository.deleteById(imageToDelete.getId());

    }

    public List<BoardResponseDto> getBoardsByKeyword(Long keywordId, String sort, int page, int limit) {
        PageRequest pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, sort));
        Page<Board> boardPage = boardRepository.findByKeywordId(keywordId, pageable);

        return boardPage.getContent().stream()
                .map(this::convertToBoardResponseDto)
                .collect(Collectors.toList());
    }


    public List<BoardResponseDto> getBoardsByUserId(String userId) {
        List<Board> boards = boardRepository.findByUserId(userId);
        return boards.stream()
                .map(this::convertToBoardResponseDto)
                .collect(Collectors.toList());
    }
}// 끝