package com.yp.puppy.api.repository.community;

import com.yp.puppy.api.entity.community.Board;
import org.springframework.data.jpa.repository.JpaRepository;
//커스텀 레포지토리 상속 시킴!
public interface BoardRepository extends JpaRepository<Board,String>,BoardRepositoryCustom {

}
