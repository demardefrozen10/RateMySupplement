package ratemysupps.readmodel;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class ReadReview {

    private Long id;

    private String username;

    private double rating;

    private String comment;

    private List<String> imageUrls;

    private String purchaseImageUrl;

    private String supplementName;

    private String supplementImageUrl;

    private LocalDateTime createdAt;

    private String variant;
}
