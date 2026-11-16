package ratemysupps.config;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ratemysupps.entity.Brand;
import ratemysupps.entity.Category;
import ratemysupps.entity.Review;
import ratemysupps.entity.Supplement;
import ratemysupps.repository.IBrandRepository;
import ratemysupps.repository.ICategoryRepository;
import ratemysupps.repository.IReviewRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Seeds brands, categories, supplements and sample reviews on startup when app.seed.enabled=true.
 * Safe to re-run: existing brands, categories and supplements (matched by name) are skipped,
 * and reviews are only added to supplements that have none.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final String RN_PRODUCT = "https://revolution-nutrition.com/product/";
    private static final String RN_IMG = "https://revolution-production.s3.amazonaws.com/storage/uploads/";
    private static final String GM_PRODUCT = "https://gorillamind.com/products/";
    private static final String GM_IMG = "https://cdn.shopify.com/s/files/1/0369/2580/0493/files/";

    private final IBrandRepository brandRepo;
    private final ICategoryRepository categoryRepo;
    private final IReviewRepository reviewRepo;

    public DataSeeder(IBrandRepository brandRepo, ICategoryRepository categoryRepo, IReviewRepository reviewRepo) {
        this.brandRepo = brandRepo;
        this.categoryRepo = categoryRepo;
        this.reviewRepo = reviewRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Category> categories = new HashMap<>();
        for (String name : List.of("Protein Powder", "Pre-Workout", "Amino Acids", "Hydration", "Creatine")) {
            categories.put(name, findOrCreateCategory(name));
        }

        seedBrand(
                "Revolution Nutrition",
                "Canadian sports nutrition brand known for real dessert-flavoured proteins, pre-workouts and aminos.",
                "https://revolution-nutrition.com",
                "https://d16ov7pfc1umf3.cloudfront.net/d7e417d7-5104-420e-a2e8-24f456edb3db/revolution/img/logo.png",
                "Canada",
                revolutionSupplements(categories)
        );

        seedBrand(
                "Gorilla Mind",
                "US supplement brand focused on heavily dosed, ingredient-transparent pre-workouts and performance products.",
                "https://gorillamind.com",
                GM_IMG + "GM_HERO_Mode_BlueRaspberry_1500x1500_23e6e7d5-1d7e-4bbf-802e-3d8ad8c24bf9.png?v=1772215014",
                "USA",
                gorillaMindSupplements(categories)
        );
    }

    private Category findOrCreateCategory(String name) {
        return categoryRepo.findByName(name).orElseGet(() -> {
            Category category = new Category();
            category.setName(name);
            return categoryRepo.save(category);
        });
    }

    private void seedBrand(String name, String description, String websiteUrl, String imageUrl,
                           String country, List<Supplement> supplements) {
        Brand brand = brandRepo.findByBrandName(name).orElseGet(() -> {
            Brand b = new Brand();
            b.setBrandName(name);
            b.setDescription(description);
            b.setWebsiteUrl(websiteUrl);
            b.setImageUrl(imageUrl);
            b.setCountry(country);
            b.setCreatedAt(LocalDateTime.now());
            b.setAverageRating(0.0);
            b.setTotalReviews(0);
            b.setVerified(true);
            b.setSupplements(new ArrayList<>());
            return b;
        });

        Set<String> existing = new HashSet<>();
        if (brand.getSupplements() == null) {
            brand.setSupplements(new ArrayList<>());
        }
        brand.getSupplements().forEach(s -> existing.add(s.getSupplementName()));

        for (Supplement supplement : supplements) {
            if (existing.contains(supplement.getSupplementName())) {
                continue;
            }
            supplement.setBrand(brand);
            brand.getSupplements().add(supplement);
        }

        brandRepo.saveAndFlush(brand);
        seedReviews(brand);
    }

    private void seedReviews(Brand brand) {
        int brandTotal = 0;
        double brandSum = 0;

        for (Supplement s : brand.getSupplements()) {
            if (reviewRepo.findBySupplementId(s.getId()).isEmpty()) {
                List<Review> reviews = buildReviews(s);
                reviewRepo.saveAll(reviews);
                s.setAverageRating(reviews.stream().mapToDouble(Review::getRating).average().orElse(0));
                s.setTotalReviews(reviews.size());
            }
            int count = s.getTotalReviews() == null ? 0 : s.getTotalReviews();
            double avg = s.getAverageRating() == null ? 0 : s.getAverageRating();
            brandTotal += count;
            brandSum += count * avg;
        }

        if (brandTotal > 0) {
            brand.setTotalReviews(brandTotal);
            brand.setAverageRating(brandSum / brandTotal);
        }
    }

    private List<Review> buildReviews(Supplement supplement) {
        Random random = new Random(supplement.getSupplementName().hashCode());
        List<String> variants = supplement.getVariants() == null ? List.of() : supplement.getVariants();

        List<SeedReviewTemplates.Tpl> pool = new ArrayList<>(
                SeedReviewTemplates.GENERAL.getOrDefault(supplement.getCategory().getName(), List.of()));
        if (!variants.isEmpty()) {
            pool.addAll(SeedReviewTemplates.FLAVOR);
        }
        Collections.shuffle(pool, random);

        List<Review> reviews = new ArrayList<>();
        for (SeedReviewTemplates.Tpl tpl : pool.subList(0, Math.min(10, pool.size()))) {
            boolean usesFlavor = tpl.text().contains("{flavor}");
            String variant = null;
            if (!variants.isEmpty() && (usesFlavor || random.nextInt(10) < 6)) {
                variant = variants.get(random.nextInt(variants.size()));
            }

            Review review = new Review();
            review.setSupplement(supplement);
            review.setRating(tpl.rating());
            review.setComment(usesFlavor ? tpl.text().replace("{flavor}", variant) : tpl.text());
            review.setVariant(variant);
            // Empty username is how the frontend submits an anonymous review.
            review.setUsername(random.nextInt(10) < 4
                    ? ""
                    : SeedReviewTemplates.NAMES.get(random.nextInt(SeedReviewTemplates.NAMES.size())));
            review.setImageUrls(new ArrayList<>());
            review.setVerified(true);
            review.setCreatedAt(LocalDateTime.now().minusDays(1 + random.nextInt(180)).minusHours(random.nextInt(24)));
            reviews.add(review);
        }
        return reviews;
    }

    private List<Supplement> revolutionSupplements(Map<String, Category> c) {
        Category protein = c.get("Protein Powder");
        Category pre = c.get("Pre-Workout");
        Category aminos = c.get("Amino Acids");
        Category hydration = c.get("Hydration");

        return List.of(
                supplement("Bull Dose Rush", pre,
                        "High energy pre-workout with Citrulline Malate, Beta-Alanine and Agmatine for pump and energy.",
                        RN_PRODUCT + "bull-dose-rush", RN_IMG + "2025/9/BDR-BP_productImage_2025.webp",
                        List.of("438g / 33 Servings"),
                        List.of("Blue Sharks", "Grape Slice", "Red Raspberry", "Sour Cherry", "Banana Pop", "Candy Peach", "Rainbow Belts")),
                supplement("High Whey", protein,
                        "Best-selling whey protein blend with 25g of protein per serving in real dessert flavours.",
                        RN_PRODUCT + "high-whey", RN_IMG + "2026/10/CA_6lbBag_HW-CNC_ProductImage.webp",
                        List.of("2lbs / 24 Servings", "6lbs / 73 Servings"),
                        List.of("Vanilla Cake", "Chai Latte", "Cookies and Cream", "Salted Caramel", "Chocolate Hazelnut",
                                "Pistachio Ice Cream", "White Chocolate Macadamia", "Cookie Dough Ice Cream", "Iced Cappuccino",
                                "Caramel Iced Cappuccino", "Peanut Butter Cup", "Strawberry Shortcake", "Chocolate Cake", "Chocolate Banana")),
                supplement("Hydration+ Electrolyte", hydration,
                        "Electrolyte hydration powder in a 50-serving tub.",
                        RN_PRODUCT + "hydration-electrolyte", RN_IMG + "2026/6/Hydration-WM_Productimage-2026.webp",
                        List.of("145g / 50 Servings"),
                        List.of("Watermelon", "Peach Mango", "Blue Raspberry", "Lemonade")),
                supplement("Iso Whey", protein,
                        "Premium whey protein isolate in real dessert flavours.",
                        RN_PRODUCT + "whey-isolate", RN_IMG + "2026/5/CA_6lbBag_IW-VC_ProductImagev1.webp",
                        List.of("2lbs / 27 Servings", "6lbs / 82 Servings"),
                        List.of("Iced Cappuccino", "Peanut Butter Cup", "Cookies and Cream", "Cookie Dough Ice Cream", "Chocolate Cake", "Vanilla Cake")),
                supplement("Pure EAA", aminos,
                        "Essential amino acid formula to support muscle growth and help prevent muscle breakdown.",
                        RN_PRODUCT + "pure-eaa", RN_IMG + "2025/10/PURE_EAA_240_CP.webp",
                        List.of("240g / 26 Servings", "1.1kg / 137 Servings"),
                        List.of("Blue Sharks", "Grape Slice", "Sour Cherry", "Candy Peach")),
                supplement("Splash Clear Isolate", protein,
                        "Refreshing clear whey isolate protein drink.",
                        RN_PRODUCT + "splash-clear-protein", RN_IMG + "2026/10/SCP-LE_1LB_Productimage-2026.webp",
                        List.of("667g / 23 Servings", "425g / 17 Servings"),
                        List.of("Blue Sharks", "Blue Raspberry", "Cherry", "Lemonade", "Grape Slice", "Red Raspberry",
                                "Candy Peach", "Sour Cherry", "Rainbow Belts", "Sour Watermelon")),
                supplement("Swole", pre,
                        "Revolution Nutrition Swole in a 1.4kg, 70-serving tub.",
                        RN_PRODUCT + "swole-144kg", RN_IMG + "2025/9/144kg_Swole-SC_ProductImage_2025.webp",
                        List.of("1.4kg / 70 Servings"),
                        List.of("Sour Cherry", "Sour Watermelon", "Orange Mango")),
                supplement("Isolate Complex", protein,
                        "Whey protein with the highest level of absorption, loaded with a key amino acid.",
                        RN_PRODUCT + "isolate-complex", RN_IMG + "2025/3/CA_6lb_IC-VICC_ProductImage.webp",
                        List.of("6lbs / 82 Servings"),
                        List.of("Chocolate Mousse Cake", "Vanilla Ice Cream Cones")),
                supplement("Juiced", pre,
                        "Pre-workout with 4x the citrulline of the original formula plus Agmatine.",
                        RN_PRODUCT + "juiced", RN_IMG + "2025/3/CA_Juiced-SC_ProductImage.webp",
                        List.of("200g / 33 Servings"),
                        List.of("Sour Cherry", "Sour Watermelon", "Sour Suckers")),
                supplement("Pure BCAA", aminos,
                        "100% instantized BCAAs in a 2:1:1 ratio with zero sugar or calories.",
                        RN_PRODUCT + "pure-bcaa", RN_IMG + "2026/9/New_2025_PBCAA_SC.webp",
                        List.of("1.1kg / 122 Servings"),
                        List.of("Blue Sharks", "Red Raspberry", "Sour Cherry", "Candy Peach")),
                supplement("Pure Isolate", protein,
                        "100% natural isolate protein with 35g per serving.",
                        RN_PRODUCT + "pure-isolate", RN_IMG + "2026/11/us_PureIsolate_4lb_productImage-CC.webp",
                        List.of("2kg / 47 Servings"),
                        List.of("Chocolate", "Vanilla")),
                supplement("Splash Clear Isolate Cocktail", protein,
                        "Cocktail-inspired clear whey isolate.",
                        RN_PRODUCT + "isolate-splash-clear-cocktail", RN_IMG + "2025/9/SCP-Cocktail-SD_ProductImage_2025.webp",
                        List.of("667g / 23 Servings"),
                        List.of("Strawberry Daiquiri", "Mango Margarita", "Sex on the Beach", "Pina Colada Cocktail")),
                supplement("Vegan Smart", protein,
                        "Plant-based protein powder.",
                        RN_PRODUCT + "uprising-vegan-smart", RN_IMG + "2025/7/CA_2lb_VS-CDP_ProductImage.webp",
                        List.of("2lbs"),
                        List.of())
        );
    }

    private List<Supplement> gorillaMindSupplements(Map<String, Category> c) {
        Category pre = c.get("Pre-Workout");
        Category creatine = c.get("Creatine");

        return List.of(
                supplement("Gorilla Mode", pre,
                        "Flagship stimulant pre-workout with over 40g of actives per max daily dose for energy, focus and pumps.",
                        GM_PRODUCT + "gorilla-mode",
                        GM_IMG + "GM_HERO_Mode_BlueRaspberry_1500x1500_23e6e7d5-1d7e-4bbf-802e-3d8ad8c24bf9.png?v=1772215014",
                        List.of(),
                        List.of("Blackberry Lemonade", "Candy Apple", "Fruit Punch", "Red Gummy Fish", "White Gummy Bear", "Rocket Frost",
                                "Tiger's Blood", "Mouthwatering Watermelon", "Blue Raspberry", "Rainbow Sherbet")),
                supplement("Gorilla Mode Nitric", pre,
                        "Stimulant-free pump pre-workout with 10g citrulline, agmatine, nitrates, creatine and electrolytes.",
                        GM_PRODUCT + "gorilla-mode-nitric",
                        GM_IMG + "GM_HERO_Nitric_VolcanoBurst_1500x1500_52725aa5-3fe1-4b76-ac6b-fb24f6e95e12.png?v=1772215029",
                        List.of(),
                        List.of("Volcano Burst", "Cherry Blackout", "Fruit Punch", "Rainbow Sherbet", "Red Gummy Fish", "Tiger's Blood",
                                "White Gummy Bear", "Rocket Frost", "Blue Raspberry", "Blackberry Lemonade", "Candy Apple")),
                supplement("Gorilla Mode Base", pre,
                        "Budget-friendly, creatine-free daily pre-workout with high doses of citrulline, tyrosine and caffeine.",
                        GM_PRODUCT + "gorilla-mode-base",
                        GM_IMG + "Mode_Base_20_Scoop_BlueRaspberry_110425.png?v=1762987148",
                        List.of(),
                        List.of("Blue Raspberry", "Mouthwatering Watermelon", "Rocket Frost", "White Gummy Bear", "Sour Gummy Worm")),
                supplement("Creatine Monohydrate", creatine,
                        "Standalone creatine monohydrate for daily saturation dosing.",
                        GM_PRODUCT + "creatine",
                        GM_IMG + "GM_HERO_CreatinePowder_1500x1500_732be030-40ca-471d-a293-77032d85ed1f.png?v=1777917957",
                        List.of("100 Servings"),
                        List.of()),
                supplement("Creatine HCl", creatine,
                        "Highly soluble, stomach-friendly creatine HCl with pepsin and 2.5g scoops.",
                        GM_PRODUCT + "creatine-hcl",
                        GM_IMG + "CreatineHCl.png?v=1756332157",
                        List.of("100 Servings"),
                        List.of())
        );
    }

    private Supplement supplement(String name, Category category, String description, String websiteUrl,
                                  String imageUrl, List<String> servingSizes, List<String> variants) {
        Supplement s = new Supplement();
        s.setSupplementName(name);
        s.setCategory(category);
        s.setDescription(description);
        s.setWebsiteUrl(websiteUrl);
        s.setImageUrl(new ArrayList<>(List.of(imageUrl)));
        s.setServingSizes(new ArrayList<>(servingSizes));
        s.setVariants(new ArrayList<>(variants));
        s.setAverageRating(0.0);
        s.setTotalReviews(0);
        s.setVerified(true);
        s.setCreatedAt(LocalDateTime.now());
        return s;
    }
}
