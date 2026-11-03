package com.example.nonprofitmanagementsystem.config;

import com.example.nonprofitmanagementsystem.beneficiary.AssistanceType;
import com.example.nonprofitmanagementsystem.beneficiary.BeneficiaryManager;
import com.example.nonprofitmanagementsystem.beneficiary.Resource;
import com.example.nonprofitmanagementsystem.beneficiary.ResourceCategory;
import com.example.nonprofitmanagementsystem.event.Event;
import com.example.nonprofitmanagementsystem.event.EventManager;
import com.example.nonprofitmanagementsystem.event.EventType;
import com.example.nonprofitmanagementsystem.user.Administrator;

import java.time.LocalDateTime;

// Phase 1 only: events and resources are not stored in the database yet, and admin pages
// come in phase 2, so the app starts with this hardcoded data instead.
public class DemoData {

    public static void load(AppContext context) {
        // Not a real account; only used to pass the admin checks while seeding
        Administrator systemAdmin = new Administrator(0, "System", "Admin",
                "admin@hopefoundation.org", "", "", "");
        loadEvents(context.getEventManager(), systemAdmin);
        loadResources(context.getBeneficiaryManager());
    }

    private static void loadEvents(EventManager eventManager, Administrator admin) {
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        eventManager.createEvent(new Event(1, "Winter Clothes Fundraiser",
                "Raising funds to buy winter clothes for families in need.",
                EventType.FUNDRAISER, now.plusDays(5).withHour(18), "Community Hall, Cairo", 100), admin);

        Event workshop = new Event(2, "CV Writing Workshop",
                "Free workshop on writing a CV and preparing for interviews.",
                EventType.WORKSHOP, now.plusDays(8).withHour(11), "Online", 30);
        workshop.addRequiredSkill("Teaching");
        eventManager.createEvent(workshop, admin);

        Event outreach = new Event(3, "Food Basket Distribution",
                "Delivering food baskets to registered families.",
                EventType.OUTREACH_PROGRAM, now.plusDays(12).withHour(9), "Giza", 50);
        outreach.addRequiredSkill("Driving");
        eventManager.createEvent(outreach, admin);

        eventManager.createEvent(new Event(4, "Charity Run",
                "A 5 km run; all registration fees go to the education fund.",
                EventType.FUNDRAISER, now.plusDays(20).withHour(7), "Al-Azhar Park, Cairo", 200), admin);

        // Small capacity, so it is easy to show an event becoming FULL
        eventManager.createEvent(new Event(5, "Volunteer Orientation",
                "Introduction session for new volunteers.",
                EventType.OTHER, now.plusDays(3).withHour(16), "Hope Foundation Office", 2), admin);
    }

    private static void loadResources(BeneficiaryManager beneficiaryManager) {
        ResourceCategory food = new ResourceCategory(1, "Food", "Food supplies");
        ResourceCategory clothing = new ResourceCategory(2, "Clothing", "Clothes and blankets");
        ResourceCategory education = new ResourceCategory(3, "Education", "School supplies and tutoring");

        beneficiaryManager.addResource(new Resource(1, "Food Basket", food, AssistanceType.GOODS, 50, "basket",
                "Monthly basket of basic food items"));
        beneficiaryManager.addResource(new Resource(2, "Winter Blanket", clothing, AssistanceType.GOODS, 30, "piece",
                "Warm blanket"));
        beneficiaryManager.addResource(new Resource(3, "School Supplies Kit", education, AssistanceType.GOODS, 40, "kit",
                "Notebooks, pens and a school bag"));
        beneficiaryManager.addResource(new Resource(4, "Tutoring Session", education, AssistanceType.SERVICE, 20, "session",
                "One-hour tutoring session for a child"));
    }
}
