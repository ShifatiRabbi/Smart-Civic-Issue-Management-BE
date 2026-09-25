package com.sr.smart_civic_platform.user.repository;

import com.sr.smart_civic_platform.user.entity.User;
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
 */
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}