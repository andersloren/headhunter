package se.sprinta.headhunterbackend.system;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.sprinta.headhunterbackend.ad.Ad;
import se.sprinta.headhunterbackend.ad.AdRepository;
import se.sprinta.headhunterbackend.ad.AdService;
import se.sprinta.headhunterbackend.job.Job;
import se.sprinta.headhunterbackend.job.JobService;
import se.sprinta.headhunterbackend.job.dto.JobDtoFormAdd;
import se.sprinta.headhunterbackend.user.User;
import se.sprinta.headhunterbackend.user.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Database entries (User objects, Job objects, and Ad objects) for demoing and debugging purposes.
 */

@Component
@Transactional
@Profile("passive")
public class DBDataFullInitializer implements CommandLineRunner {

    private final UserService userService;
    private final JobService jobService;
    private final AdRepository adRepository;
    private final AdService adService;

    // TODO: 14/03/2024 Should ads be removed?
    private final List<Ad> ads = new ArrayList<>();
    private final List<Ad> ads1 = new ArrayList<>();
    private final List<Ad> ads2 = new ArrayList<>();
    private final List<Ad> ads3 = new ArrayList<>();
    private final List<Ad> ads4 = new ArrayList<>();
    private final List<Ad> ads5 = new ArrayList<>();

    public DBDataFullInitializer(UserService userService, JobService jobService, AdRepository adRepository, AdService adService) {
        this.userService = userService;
        this.jobService = jobService;
        this.adRepository = adRepository;
        this.adService = adService;
    }

    @Override
    public void run(String... args) throws Exception {

        // TODO: 14/03/2024 Fix these duplicates
        User user1 = new User();
        user1.setEmail("m@e.se");
        user1.setUsername("Mikael");
        user1.setPassword("a");
        user1.setRoles("admin user");

        User user2 = new User();
        user2.setEmail("a@l.se");
        user2.setUsername("Anders");
        user2.setPassword("a");
        user2.setRoles("user");

        this.userService.save(user1);
        this.userService.save(user2);

        Job job1 = this.jobService.addJob(new JobDtoFormAdd("m@e.se", "job1 Title", "job1 Description", "job1 Instruction"));
        Job job2 = this.jobService.addJob(new JobDtoFormAdd("m@e.se", "job2 Title", "job2 Description", "job2 Instruction"));
        Job job3 = this.jobService.addJob(new JobDtoFormAdd("m@e.se", "job3 Title", "job3 Description", "job3 Instruction"));
        Job job4 = this.jobService.addJob(new JobDtoFormAdd("a@l.se", "job4 Title", "job4 Description", "job4 Instruction"));
        Job job5 = this.jobService.addJob(new JobDtoFormAdd("a@l.se", "job5 Title", "job5 Description", "job5 Instruction"));

        user1.addJob(job1);
        user1.addJob(job2);
        user1.addJob(job3);
        user2.addJob(job4);
        user2.addJob(job5);

        user1.setNumberOfJobs();
        user2.setNumberOfJobs();

        Ad ad1 = this.adService.addAd(1L, new Ad("htmlCode 1"));
        Thread.sleep(1);
        Ad ad2 = this.adService.addAd(1L, new Ad("htmlCode 2"));
        Thread.sleep(1);
        Ad ad3 = this.adService.addAd(1L, new Ad("htmlCode 3"));
        Thread.sleep(1);
        Ad ad4 = this.adService.addAd(1L, new Ad("htmlCode 4"));
        Thread.sleep(1);
        Ad ad5 = this.adService.addAd(1L, new Ad("htmlCode 5"));
        Thread.sleep(1);
        Ad ad6 = this.adService.addAd(2L, new Ad("htmlCode 6"));
        Thread.sleep(1);
        Ad ad7 = this.adService.addAd(2L, new Ad("htmlCode 7"));
        Thread.sleep(1);
        Ad ad8 = this.adService.addAd(3L, new Ad("htmlCode 8"));
        Thread.sleep(1);
        Ad ad9 = this.adService.addAd(4L, new Ad("htmlCode 9"));
        Thread.sleep(1);
        Ad ad10 = this.adService.addAd(4L, new Ad("htmlCode 10"));
        Thread.sleep(1);
        Ad ad11 = this.adService.addAd(5L, new Ad("htmlCode 11"));
        Thread.sleep(1);

        ad1.setJob(job1);
        ad2.setJob(job1);
        ad3.setJob(job1);
        ad4.setJob(job1);
        ad5.setJob(job1);
        ad6.setJob(job2);
        ad7.setJob(job3);
        ad8.setJob(job3);
        ad9.setJob(job4);
        ad10.setJob(job4);
        ad11.setJob(job5);

        this.ads.add(ad1);
        this.ads.add(ad2);
        this.ads.add(ad3);
        this.ads.add(ad4);
        this.ads.add(ad5);
        this.ads.add(ad6);
        this.ads.add(ad7);
        this.ads.add(ad8);
        this.ads.add(ad9);
        this.ads.add(ad10);
        this.ads.add(ad11);

        this.jobService.save(job1);
        this.jobService.save(job2);
        this.jobService.save(job3);
        this.jobService.save(job4);
        this.jobService.save(job5);

//        List<Ad> ads1 = new ArrayList<>();
        this.ads1.add(ad1);
        this.ads1.add(ad2);
        this.ads1.add(ad3);
        this.ads1.add(ad4);
        this.ads1.add(ad5);

        job1.setAds(ads1);

//        List<Ad> ads2 = new ArrayList<>();
        ads2.add(ad6);
        ads2.add(ad7);
        job2.setAds(ads2);

//        List<Ad> ads3 = new ArrayList<>();
        ads3.add(ad8);
        job3.setAds(ads3);

//        List<Ad> ads4 = new ArrayList<>();
        ads4.add(ad9);
        ads4.add(ad10);
        job4.setAds(ads4);

//        List<Ad> ads5 = new ArrayList<>();
        ads5.add(ad11);
        job5.setAds(ads5);

        this.adRepository.save(ad1);
        this.adRepository.save(ad2);
        this.adRepository.save(ad3);
        this.adRepository.save(ad4);
        this.adRepository.save(ad5);
        this.adRepository.save(ad6);
        this.adRepository.save(ad7);
        this.adRepository.save(ad8);
        this.adRepository.save(ad9);
        this.adRepository.save(ad10);
        this.adRepository.save(ad11);
    }
}