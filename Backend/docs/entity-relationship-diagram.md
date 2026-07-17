# Entity Relationship Diagram

```mermaid
erDiagram
  BRAND ||--o{ SUPPLEMENT : has
  CATEGORY ||--o{ SUPPLEMENT : groups
  SUPPLEMENT ||--o{ REVIEW : receives
  SUPPLEMENT ||--o{ SUPPLEMENT_TAG : uses
  TAG ||--o{ SUPPLEMENT_TAG : labels

  BRAND {
    Long id PK
    String brandName UK
    String description
    String websiteUrl UK
    String imageUrl UK
    String country
    LocalDateTime createdAt
    Double averageRating
    Integer totalReviews
    boolean isVerified
  }

  CATEGORY {
    Long id PK
    String name UK
  }

  SUPPLEMENT {
    Long id PK
    Long brand_id FK
    Long category_id FK
    String supplementName
    Double averageRating
    Integer totalReviews
    String description
    boolean isVerified
    LocalDateTime createdAt
    String[] imageUrl
    String[] servingSizes
    String websiteUrl
    String[] variants
  }

  REVIEW {
    Long id PK
    Long supplement_id FK
    String username
    double rating
    String comment
    boolean isVerified
    String[] imageUrls
    String purchaseImageUrl
    LocalDateTime createdAt
    String variant
  }

  TAG {
    Long id PK
    String name UK
  }

  SUPPLEMENT_TAG {
    Long supplement_id FK
    Long tag_id FK
  }
```

## Notes

- `supplement_tag` is the join table created by `Supplement.tags`.
- `imageUrl`, `servingSizes`, `variants`, and `imageUrls` are represented as array-like fields because the Java entities use `List<String>`.
- `UK` marks fields declared with `unique = true`.
