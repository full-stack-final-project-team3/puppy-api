package com.yp.puppy.api.repository.community;

import com.yp.puppy.api.entity.community.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,String> {

}
