package com.sr.smart_civic_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/*
 * Purpose:
 * MongoDB entity তে @CreatedDate / @LastModifiedDate annotation
 * automatic populate হওয়ার জন্য auditing enable করা।
 *
 * Why:
 * প্রতিটা entity (Complaint, User, ইত্যাদি) তে "কখন তৈরি হলো",
 * "কখন শেষ update হলো" ম্যানুয়ালি সেট করতে হবে না।
 *
 * Future:
 * @CreatedBy / @LastModifiedBy ব্যবহার করতে হলে (কোন user create করলো)
 * এখানে AuditorAware<String> bean যোগ করতে হবে —
 * সেইটা PHASE-2 (Auth) শেষ হওয়ার পর করা হবে, কারণ তখনই
 * "logged-in user কে" জানার উপায় থাকবে (SecurityContext থেকে)।
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}