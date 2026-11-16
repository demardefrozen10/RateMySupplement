package ratemysupps.config;

import java.util.List;
import java.util.Map;

/** Sample review text for DataSeeder. Comments must stay within the 200 character column limit. */
final class SeedReviewTemplates {

    record Tpl(double rating, String text) {}

    static final List<String> NAMES = List.of(
            "Jake M.", "Priya", "Marcus T.", "Sarah K.", "Dan", "Alex R.", "Mike", "Jess",
            "Chris P.", "Liam", "Olivia", "Noah B.", "Emma", "Tyler", "Ben S.", "Hannah");

    static final List<Tpl> FLAVOR = List.of(
            new Tpl(5, "The {flavor} flavour is fantastic. Easily my favourite so far."),
            new Tpl(4, "{flavor} tastes great, a little sweet but really good ice cold."),
            new Tpl(3, "{flavor} was just okay for me. A bit artificial tasting."),
            new Tpl(5, "Tried {flavor} and it did not disappoint. Mixes great with water."),
            new Tpl(2, "{flavor} had a strange aftertaste for me. Would try another flavour next time."),
            new Tpl(4, "{flavor} is solid. Not too sweet and easy to drink."));

    static final Map<String, List<Tpl>> GENERAL = Map.of(
            "Protein Powder", List.of(
                    new Tpl(5, "Mixes smooth with no clumps and no chalky aftertaste. An easy daily staple for me."),
                    new Tpl(5, "Best tasting protein I've tried. I actually look forward to my post-workout shake."),
                    new Tpl(4, "Solid protein for the price. Digests well and no bloating for me."),
                    new Tpl(5, "Great macros and does exactly what it should. Recovery has been noticeably better."),
                    new Tpl(4, "Good flavour and mixes well in a shaker. Would like more flavour options."),
                    new Tpl(3, "Decent protein but a little too sweet for my taste. Works fine overall."),
                    new Tpl(5, "Been using it for months and the tub lasts a long time. Smooth and easy on the stomach."),
                    new Tpl(2, "Left a few clumps even with a shaker ball. Taste was okay but I expected smoother."),
                    new Tpl(5, "Switched from my old brand and haven't looked back. Smooth, tasty and consistent."),
                    new Tpl(4, "Good value per serving and the scoop is easy to measure. Shipping was quick."),
                    new Tpl(3, "Average. Nothing wrong with it but nothing that makes it stand out either."),
                    new Tpl(4, "Reliable quality every time. Flavour is consistent from tub to tub."),
                    new Tpl(5, "No stomach issues at all, which is rare for me with protein powders. Highly recommend."),
                    new Tpl(4, "Mixes fast and tastes good cold. Helps me hit my daily protein goal.")),
            "Pre-Workout", List.of(
                    new Tpl(5, "Clean energy and great focus for about two hours. No crash afterwards."),
                    new Tpl(5, "The pump is insane. Noticeably better lifts and I feel locked in from the first set."),
                    new Tpl(4, "Strong but not jittery. I take one scoop and it's plenty for me."),
                    new Tpl(3, "Decent energy but a mild crash after an hour. Works better on an empty stomach."),
                    new Tpl(5, "Best pre-workout I've used in a while. Mixes easily and the energy is smooth."),
                    new Tpl(4, "Good for early morning sessions. Gets me out of bed and into the gym."),
                    new Tpl(2, "Gave me tingles and a mild headache. A bit too intense for my tolerance."),
                    new Tpl(4, "Great value for the number of servings. I take a half scoop on lighter days."),
                    new Tpl(5, "Pumps and endurance have both improved. Finishing sets feels way easier."),
                    new Tpl(3, "Tastes fine and works okay, but I didn't feel a huge difference from my old one."),
                    new Tpl(4, "No stomach upset which is a big plus. Kicks in about 20 minutes after."),
                    new Tpl(5, "Focus is next level. I get through long leg days without fading."),
                    new Tpl(4, "Mixes well with no gritty residue. Would buy again."),
                    new Tpl(1, "Didn't do much for me and I had trouble sleeping after an evening session.")),
            "Amino Acids", List.of(
                    new Tpl(5, "Great for sipping during workouts. Tastes like a flavoured drink and keeps me going."),
                    new Tpl(4, "Mixes instantly with no clumps. I use it during fasted training."),
                    new Tpl(5, "Noticeably less soreness the day after heavy training sessions."),
                    new Tpl(4, "Good flavour and zero sugar. Perfect for my cutting phase."),
                    new Tpl(3, "Works fine but the taste is a bit strong. Needs extra water."),
                    new Tpl(5, "Dissolves fully and doesn't leave any residue. Easy to drink all session long."),
                    new Tpl(4, "Good value for the servings. Helps me get through long gym days."),
                    new Tpl(2, "Flavour was too artificial for me and a little sour. Mixes fine though."),
                    new Tpl(5, "My go-to during training. Light, refreshing and doesn't upset my stomach."),
                    new Tpl(4, "Solid recovery support. I notice it most after leg days."),
                    new Tpl(3, "Not sure I notice a huge difference, but taste and mixability are good."),
                    new Tpl(5, "Tastes great and mixes easily. Will be reordering."),
                    new Tpl(4, "Convenient scoop and consistent quality. Does what it says.")),
            "Hydration", List.of(
                    new Tpl(5, "Perfect for hot days and long sessions. No more cramping or headaches."),
                    new Tpl(4, "Light flavour and not overly sweet. I drink one every afternoon."),
                    new Tpl(5, "Tastes like a light fruit drink and keeps me hydrated all day."),
                    new Tpl(3, "Good electrolytes but a bit salty for me. Fine in more water."),
                    new Tpl(5, "Great value for 50 servings. Easy to mix and no clumps."),
                    new Tpl(4, "Helped me a lot during summer training. Noticeably less fatigue."),
                    new Tpl(2, "A little too salty for my taste and I needed to dilute it a lot."),
                    new Tpl(4, "Dissolves quickly and doesn't have a weird aftertaste."),
                    new Tpl(5, "Replaced my sports drinks completely. Zero sugar and tastes great."),
                    new Tpl(4, "Good for workouts and rough mornings alike. Does the job."),
                    new Tpl(5, "Reliable and refreshing. I take it to the gym every session."),
                    new Tpl(3, "Decent but I prefer a stronger flavour. Works well otherwise.")),
            "Creatine", List.of(
                    new Tpl(5, "Mixes into anything and has no taste. Strength has steadily gone up since I started."),
                    new Tpl(4, "Great value for 100 servings. Dissolves easily in water or juice."),
                    new Tpl(5, "Zero stomach issues and noticeably better performance after a few weeks."),
                    new Tpl(4, "Quality powder and fine texture. Does exactly what creatine should do."),
                    new Tpl(3, "Works fine but takes a while to dissolve in cold water. Stir well."),
                    new Tpl(5, "My daily staple. I take it every morning with no issues."),
                    new Tpl(4, "Consistent quality and fast shipping. Good price for what you get."),
                    new Tpl(5, "Noticed better endurance and an extra rep or two on heavy lifts."),
                    new Tpl(2, "A little clumpy at the bottom of the tub after a few weeks."),
                    new Tpl(4, "No bloating for me and easy to measure with the scoop."),
                    new Tpl(5, "Simple, clean and effective. Would recommend to anyone starting creatine."),
                    new Tpl(3, "I didn't notice a big change but I'm only a month in.")));

    private SeedReviewTemplates() {}
}
