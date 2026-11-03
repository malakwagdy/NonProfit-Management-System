package com.example.nonprofitmanagementsystem.config;

import com.example.nonprofitmanagementsystem.beneficiary.BeneficiaryManager;
import com.example.nonprofitmanagementsystem.communication.CommunicationManager;
import com.example.nonprofitmanagementsystem.communication.EmailSender;
import com.example.nonprofitmanagementsystem.communication.FormattingDecorator;
import com.example.nonprofitmanagementsystem.communication.LoggingDecorator;
import com.example.nonprofitmanagementsystem.communication.NotificationChannel;
import com.example.nonprofitmanagementsystem.communication.SMSSender;
import com.example.nonprofitmanagementsystem.donation.CheckDonationCreator;
import com.example.nonprofitmanagementsystem.donation.CheckDonationStrategy;
import com.example.nonprofitmanagementsystem.donation.DonationManager;
import com.example.nonprofitmanagementsystem.donation.InKindDonationCreator;
import com.example.nonprofitmanagementsystem.donation.InKindDonationStrategy;
import com.example.nonprofitmanagementsystem.donation.ItemCategory;
import com.example.nonprofitmanagementsystem.donation.OnlineDonationCreator;
import com.example.nonprofitmanagementsystem.donation.OnlineDonationStrategy;
import com.example.nonprofitmanagementsystem.event.EventManager;
import com.example.nonprofitmanagementsystem.user.Beneficiary;
import com.example.nonprofitmanagementsystem.user.User;
import com.example.nonprofitmanagementsystem.user.UserManager;
import com.example.nonprofitmanagementsystem.user.Volunteer;
import com.example.nonprofitmanagementsystem.volunteer.VolunteerManager;

import java.util.ArrayList;
import java.util.Arrays;

// Singleton: holds the one shared instance of every manager and the logged-in user,
// so every GUI controller works on the same data.
public class AppContext {
    private static AppContext instance;

    private final CommunicationManager communicationManager;
    private final UserManager userManager;
    private final EventManager eventManager;
    private final DonationManager donationManager;
    private final VolunteerManager volunteerManager;
    private final BeneficiaryManager beneficiaryManager;

    private User currentUser;
    // Users who logged in during this run (see login)
    private final ArrayList<User> sessionUsers = new ArrayList<>();

    private AppContext() {
        ConfigurationManager config = ConfigurationManager.getInstance();

        communicationManager = new CommunicationManager();
        // Decorator: emails get the organization header and signature, and every message is logged to a file
        communicationManager.addChannel(NotificationChannel.EMAIL,
                new LoggingDecorator(
                        new FormattingDecorator(
                                new EmailSender("smtp.hopefoundation.org", config.getOrganizationEmail()),
                                config.getOrganizationName(),
                                "Kind regards,\n" + config.getOrganizationName()),
                        "notifications.log"));
        communicationManager.addChannel(NotificationChannel.SMS, new SMSSender("DemoSMS", "+201000000000"));

        userManager = new UserManager();
        eventManager = new EventManager(communicationManager);
        volunteerManager = new VolunteerManager(communicationManager, userManager);
        beneficiaryManager = new BeneficiaryManager(communicationManager, userManager);

        donationManager = new DonationManager(communicationManager, userManager);
        donationManager.registerCreator(new OnlineDonationCreator());
        donationManager.registerCreator(new CheckDonationCreator());
        donationManager.registerCreator(new InKindDonationCreator());
        donationManager.setDonationStrategy(new OnlineDonationStrategy("DemoPay"));
        donationManager.setDonationStrategy(new CheckDonationStrategy(true));
        donationManager.setDonationStrategy(new InKindDonationStrategy(Arrays.asList(ItemCategory.values())));

        DemoData.load(this);
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    public User login(String email, String password) {
        User user = userManager.login(email, password);

        // The database returns a new object on every fetch. If this user already logged in during
        // this run, keep using the same object, so their registrations, subscriptions and requests
        // still point to them.
        for (User existing : sessionUsers) {
            if (existing.getUserId() == user.getUserId()) {
                currentUser = existing;
                return currentUser;
            }
        }

        sessionUsers.add(user);
        if (user instanceof Volunteer) volunteerManager.registerVolunteer((Volunteer) user);
        if (user instanceof Beneficiary) beneficiaryManager.registerBeneficiary((Beneficiary) user);
        currentUser = user;
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() { return currentUser; }

    public CommunicationManager getCommunicationManager() { return communicationManager; }
    public UserManager getUserManager() { return userManager; }
    public EventManager getEventManager() { return eventManager; }
    public DonationManager getDonationManager() { return donationManager; }
    public VolunteerManager getVolunteerManager() { return volunteerManager; }
    public BeneficiaryManager getBeneficiaryManager() { return beneficiaryManager; }
}
