package com.yp.puppy.api.repository.user;

import com.yp.puppy.api.entity.user.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergyRepository extends JpaRepository<Allergy, String> {
}
