package com.sr.smart_civic_platform.user.repository;

import com.sr.smart_civic_platform.user.entity.User;
import com.sr.smart_civic_platform.user.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/*
 * Purpose:
 * User collection এর সাথে database operation (CRUD + custom query)।
 *
 * Why Optional<User> for findByEmail:
 * User পাওয়া নাও যেতে পারে (login এ ভুল email দিলে) — Optional দিয়ে
 * null check force করা হচ্ছে, NullPointerException এড়ানোর জন্য।
 *
 * existsByEmail:
 * Register এর সময় শুধু "আছে কিনা" জানলেই চলে, পুরো User object আনার
 * দরকার নেই — এইটা lightweight এবং faster (MongoDB শুধু boolean count check করে)।
 *
 * User collection এর CRUD + custom queries।
 *
 * New in this step:
 * findByRole - admin এর role filter করে user list দেখার জন্য।
 * Spring Data নিজেই method name থেকে query বানিয়ে ফেলে
 * (query annotation লেখার দরকার নেই এই simple case এ)। 
*/
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole(UserRole role, Pageable pageable);
}