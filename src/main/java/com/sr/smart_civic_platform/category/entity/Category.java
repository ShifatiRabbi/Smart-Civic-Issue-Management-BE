package com.sr.smart_civic_platform.category.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/*
 * Purpose:
 * Complaint categories (Road, Drain, Garbage, Water, Street Light ইত্যাদি)।
 *
 * Why not an enum:
 * Enum হলে নতুন category যোগ করতে code change + redeploy লাগতো।
 * Database-driven রাখলে admin runtime এ নতুন category যোগ করতে পারবে।
 *
 * Why soft-delete (isActive) instead of real delete:
 * Category future এ complaints থেকে referenced হবে (categoryId)।
 * Hard delete করলে পুরনো complaint এর reference broken হয়ে যাবে।
 * isActive=false করলে নতুন complaint এ select করা যাবে না,
 * কিন্তু পুরনো data/reference অক্ষত থাকে।
 */
@Document(collection = "categories")
public class Category {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String description;

    @Indexed
    private boolean isActive;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Category() {
    }

    public Category(String name, String description, boolean isActive) {
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}