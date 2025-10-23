package com.example.startlight.memoryAlbum.service;

import com.example.startlight.pet.dao.PetDao;
import com.example.startlight.pet.entity.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MemoryAlbumScheduleService {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//    private final MemoryAlbumFlaskService memoryAlbumFlaskService;
    private final PetDao petDao;


    // 추억별 기반 생성
    public void createAlbumAfterOneDay(Long petId) {

        // ✅ 하루 후 실행 시간 설정 (24시간 = 86,400,000 ms)
        long initialDelay = TimeUnit.DAYS.toMillis(1);

        System.out.println("✅ Task will execute after: " + initialDelay + " ms");

        // ✅ 한 번만 실행되도록 설정
        executor.schedule(
                () -> executeTask(petId), // ✅ 실행할 작업을 람다로 감싸기
                initialDelay,
                TimeUnit.MILLISECONDS
        );
    }

    // ✅ 처음 실행까지의 남은 시간 계산
    private long getInitialDelay(LocalDateTime albumStartedTime) {
        LocalDateTime now = LocalDateTime.now();

        // 만약 입력받은 시간이 현재 시간보다 이전이라면 다음 주기로 설정
        if (albumStartedTime.isBefore(now)) {
            //albumStartedTime = albumStartedTime.plusWeeks(1); // 다음 주기로 설정
            albumStartedTime = albumStartedTime.plusMinutes(1);
        }

        // albumStartedTime → 밀리초로 변환 후 현재 시간과 차이 계산
        return albumStartedTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                - System.currentTimeMillis();
    }

    // ✅ 실행할 작업 함수
    private void executeTask(Long petId) {
        System.out.println("📅 Task executed at: " + LocalDateTime.now());
//        memoryAlbumFlaskService.generateMemoryAlbum(petId,0);
    }

    // ✅ 작업 종료 (필요 시 호출)
    public void stopScheduler() {
        if (!executor.isShutdown()) {
            System.out.println("🛑 Stopping scheduler...");
            executor.shutdown();
        }
    }

    // ✅ 생일, 기일 생성
    public void createAlbumBirthDeath(Long petId) {
        Pet selectedPet = petDao.selectPet(petId);

        // ✅ 생일 스케줄링
        if (selectedPet.getBirth_date() != null) {
            LocalDateTime birthDateTime = getNextTargetDate(selectedPet.getBirth_date());
            long birthDelay = getDelayUntil(birthDateTime);

            System.out.println("🎯 Birth Task scheduled for: " + birthDateTime);
            System.out.println("🚀 Birth task will be executed in: " + birthDelay + " ms");

            executor.schedule(() -> {
                System.out.println("🎉 Birth Task executed at: " + LocalDateTime.now());
                executeBirthTask(petId);
            }, birthDelay, TimeUnit.MILLISECONDS);
        }

        // ✅ 기일 스케줄링
        if (selectedPet.getDeath_date() != null) {
            LocalDateTime deathDateTime = getNextTargetDate(selectedPet.getDeath_date());
            long deathDelay = getDelayUntil(deathDateTime);

            System.out.println("🎯 Death Task scheduled for: " + deathDateTime);
            System.out.println("🚀 Death task will be executed in: " + deathDelay + " ms");

            executor.schedule(() -> {
                System.out.println("🎉 Death Task executed at: " + LocalDateTime.now());
                executeDeathTask(petId);
            }, deathDelay, TimeUnit.MILLISECONDS);
        }
    }

    // ✅ 날짜 변환 후 실행 시간 계산
    private LocalDateTime getNextTargetDate(String date) {
        LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 올해 날짜로 변환
        targetDate = targetDate.withYear(LocalDate.now().getYear());

        // 올해 날짜가 이미 지났으면 내년으로 설정
        if (targetDate.isBefore(LocalDate.now())) {
            targetDate = targetDate.plusYears(1);
        }

        return targetDate.atTime(LocalTime.of(0, 0)); // 00:00:00 실행
    }

    // ✅ 현재 시간과의 차이 계산 (밀리초 단위)
    private long getDelayUntil(LocalDateTime targetDateTime) {
        return Duration.between(LocalDateTime.now(), targetDateTime).toMillis();
    }

    // ✅ 생일 처리 함수
    private void executeBirthTask(Long petId) {
        System.out.println("🎂 Executing Birth Task for petId: " + petId);
        // 실행 로직 추가
//        memoryAlbumFlaskService.generateMemoryAlbum(petId,1);
    }

    // ✅ 기일 처리 함수
    private void executeDeathTask(Long petId) {
        System.out.println("🕯️ Executing Death Task for petId: " + petId);
        // 실행 로직 추가
//        memoryAlbumFlaskService.generateMemoryAlbum(petId,2);
    }

    // 랜덤 생성 (일주일마다 반복 실행)
    // 펫 생성 후 바로 다음날부터
    public void createAlbumRandom(Long petId) {

        // ✅ 하루 후 실행 시간 설정 (24시간 = 86,400,000 ms)
        long initialDelay = TimeUnit.DAYS.toMillis(1);

        // ✅ 일주일 간격 설정 (7일 = 604,800,000 ms)
        long period = TimeUnit.DAYS.toMillis(7);

        System.out.println("✅ First task will execute after: " + initialDelay + " ms");
        System.out.println("✅ Task will repeat every: " + period + " ms");

        // ✅ 일정 간격으로 반복 실행하도록 스케줄링
        executor.scheduleAtFixedRate(
                () -> executeTaskRandom(petId), // ✅ 실행할 작업을 람다로 감싸기
                initialDelay,  // ✅ 첫 실행까지의 대기 시간
                period,        // ✅ 반복 간격 설정 (일주일)
                TimeUnit.MILLISECONDS
        );
    }

    private void executeTaskRandom(Long petId) {
        System.out.println("Executing Random Task for petId: " + petId);
        // 실행 로직 추가
//        memoryAlbumFlaskService.generateMemoryAlbum(petId,3);
    }
}
