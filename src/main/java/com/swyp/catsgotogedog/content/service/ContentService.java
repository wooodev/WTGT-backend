package com.swyp.catsgotogedog.content.service;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.content.domain.entity.*;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.FestivalInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.LodgeInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.RestaurantInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.SightsInformation;
import com.swyp.catsgotogedog.content.domain.request.ContentRequest;
import com.swyp.catsgotogedog.content.domain.response.ContentImageResponse;
import com.swyp.catsgotogedog.content.domain.response.LastViewHistoryResponse;
import com.swyp.catsgotogedog.content.domain.response.PlaceDetailResponse;
import com.swyp.catsgotogedog.content.repository.*;
import com.swyp.catsgotogedog.mypage.domain.entity.LastViewHistory;
import com.swyp.catsgotogedog.mypage.domain.entity.LastViewHistoryId;
import com.swyp.catsgotogedog.pet.domain.entity.PetGuide;
import com.swyp.catsgotogedog.pet.repository.PetGuideRepository;
import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContentService {
    private final ContentRepository contentRepository;
    private final ContentElasticRepository contentElasticRepository;
    private final ContentImageRepository contentImageRepository;
    private final ContentWishRepository contentWishRepository;
    private final ViewTotalRepository viewTotalRepository;
    private final UserRepository userRepository;
    private final ViewLogRepository viewLogRepository;
    private final VisitHistoryRepository visitHistoryRepository;
    private final PetGuideRepository petGuideRepository;
    private final LastViewHistoryRepository lastViewHistoryRepository;
    private final LodgeInformationRepository lodgeInformationRepository;
    private final RestaurantInformationRepository restaurantInformationRepository;
    private final SightsInformationRepository sightsInformationRepository;
    private final FestivalInformationRepository festivalInformationRepository;

    private final ContentSearchService contentSearchService;

    public void saveContent(ContentRequest request){
        Content content = Content.builder()
                .categoryId(request.getCategoryId())
                .addr1(request.getAddr1())
                .addr2(request.getAddr2())
                .image(request.getImage())
                .thumbImage(request.getThumbImage())
                .copyright(request.getCopyright())
                .mapx(request.getMapx())
                .mapy(request.getMapy())
                .mLevel(request.getMlevel())
                .tel(request.getTel())
                .title(request.getTitle())
                .zipCode(request.getZipcode())
                .contentTypeId(request.getContentTypeId())
                .build();
        contentRepository.save(content);
        contentElasticRepository.save(ContentDocument.from(content));
    }

    public PlaceDetailResponse getPlaceDetail(int contentId, String userId){
        
        Content content = contentRepository.findByContentId(contentId);

        int contentTypeId = content.getContentTypeId();

        // 카테고리 공통
        viewTotalRepository.upsertAndIncrease(contentId);

        if(userId != null){
            recordView(userId, contentId);
        }

        double avg = contentSearchService.getAverageScore(contentId);

        boolean wishData = (userId != null) ? contentSearchService.getWishData(userId, contentId) : false;

        int wishCnt = contentWishRepository.countByContent_ContentId(contentId);

        boolean visited = hasVisited(userId, contentId);

        int totalView = viewTotalRepository.findTotalViewByContentId(contentId)
                .orElse(0);

        PetGuide petGuide = getPetGuide(contentId)
                .orElse(null);

        List<ContentImageResponse> detailImage = getDetailImage(contentId);

        String restDate = contentSearchService.getRestDate(contentId);

        Map<String, Object> additional = getAdditionalInfo(contentId,contentTypeId);

        return PlaceDetailResponse.from(
                content,
                avg,
                wishData,
                wishCnt,
                visited,
                totalView,
                detailImage,
                petGuide,
                restDate,
                additional);
    }

    public boolean checkWish(String userId, int contentId){
        if (userId == null || userId.isBlank()|| userId.equals("anonymousUser")) {
            return false;
        }

        validateUser(userId);

        Content content = contentRepository.findByContentId(contentId);

        boolean isWished = isWished(userId, contentId);

        if(isWished){
            contentWishRepository.deleteByUserIdAndContent(Integer.parseInt(userId),content);
            return false;
        }else{
            ContentWish cw = ContentWish.builder()
                    .userId(Integer.parseInt(userId))
                    .content(content)
                    .build();
            contentWishRepository.save(cw);
            return true;
        }
    }

    public void recordView(String userId, int contentId){

//        if (userId != null) {
//            Optional<User> user = userRepository.findById(Integer.parseInt(userId));
//        }

        Content content = contentRepository.findByContentId(contentId);
        if (content == null) {
            throw new EntityNotFoundException("contentId=" + contentId);
        }

        User user = null;
        if (userId != null && !userId.isBlank()) {
            user = userRepository.findById(Integer.parseInt(userId))
                    .orElseThrow(() -> new EntityNotFoundException("userId=" + userId));
        }

        viewLogRepository.save(
                ViewLog.builder()
                        .user(user)
                        .content(content)
                        .viewedAt(now())
                        .build()
        );
    }

    public boolean checkVisited(String userId, int contentId){
        if (userId == null || userId.isBlank()|| userId.equals("anonymousUser")) {
            return false;
        }

        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new CatsgotogedogException(ErrorCode.MEMBER_NOT_FOUND));

        Content content = contentRepository.findByContentId(contentId);

        boolean visited = hasVisited(userId, contentId);

        if(visited){
            visitHistoryRepository.deleteByUserAndContent(user,content);
            return false;
        }else{
            VisitHistory vh = VisitHistory.builder()
                    .user(user)
                    .content(content)
                    .build();
            visitHistoryRepository.save(vh);
            return true;
        }
    }


    public List<LastViewHistoryResponse> getRecentViews(String userId) {

        if (userId == null || userId.isBlank()) {
            return null;
        }

        Pageable top = PageRequest.of(0, 20);
        List<Content> contents = viewLogRepository.findRecentContentByUser(Integer.parseInt(userId), top);

        return contents.stream()
                .map(LastViewHistoryResponse::from)
                .toList();
    }

    public boolean hasVisited(String userId, int contentId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        return visitHistoryRepository.existsByUser_UserIdAndContent_ContentId(Integer.parseInt(userId), contentId);
    }

    public List<ContentImageResponse> getDetailImage(int contentId){
        Content content = contentRepository.findByContentId(contentId);
        List<ContentImage> images = contentImageRepository.findAllByContent(content);

        return images.stream()
                .map(ci -> new ContentImageResponse(
                        ci.getImageUrl(),
                        ci.getImageFilename()
                ))
                .toList();
    }


    public Optional<PetGuide> getPetGuide(int contentId) {
        if (petGuideRepository.existsByContent_ContentId(contentId)) {
            return petGuideRepository.findByContent_ContentId(contentId);
        }
        return Optional.empty();
    }

    public boolean isWished(String userId, int contentId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        return contentWishRepository.existsByUserIdAndContent_ContentId(Integer.parseInt(userId), contentId);
    }

    // 최근 본 장소 데이터 저장
    @Transactional
    public void saveLastViewedContent(String strUserId, int contentId) {
        int userId = strUserId.equals("anonymousUser") ? 0 : Integer.parseInt(strUserId);
        User user = validateUser(userId);
        Content content = validateContent(contentId);

        LastViewHistoryId id = new LastViewHistoryId(userId, contentId);
        LastViewHistory lastViewHistory = lastViewHistoryRepository.findById(id)
            .orElse(LastViewHistory.builder()
                .user(user)
                .content(content)
                .build());

        lastViewHistory.setLastViewedAt(now());

        lastViewHistoryRepository.save(lastViewHistory);
    }

    private User validateUser(String userId) {
        return userRepository.findById(Integer.parseInt(userId))
            .orElseThrow(() -> new CatsgotogedogException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private User validateUser(int userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CatsgotogedogException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Content validateContent(int contentId) {
        return contentRepository.findById(contentId)
            .orElseThrow(() -> new CatsgotogedogException(ErrorCode.CONTENT_NOT_FOUND));
    }

    private Map<String, Object> getAdditionalInfo(int contentId, int contentTypeId) {
        return switch (contentTypeId) {
            case 15 -> getFestivalInfo(contentId);
            case 32 -> getLodgeInfo(contentId);
            case 39 -> getRestaurantInfo(contentId);
            case 12 -> getSightsInfo(contentId);
            default -> Map.of();
        };
    }

    private Map<String, Object> getFestivalInfo(int contentId) {
        var e = festivalInformationRepository.findByContent_ContentId(contentId);

        if (e == null) return Map.of("type", "FESTIVAL");

        var m = new HashMap<String, Object>();

        m.put("type", "FESTIVAL");
        m.put("festivalId", e.getFestivalId());
        m.put("ageLimit", e.getAgeLimit());
        m.put("bookingPlace", e.getBookingPlace());
        m.put("discountInfo", e.getDiscountInfo());
        m.put("eventStartDate", e.getEventStartDate());
        m.put("eventEndDate", e.getEventEndDate());
        m.put("eventHomepage", e.getEventHomepage());
        m.put("eventPlace", e.getEventPlace());
        m.put("placeInfo", e.getPlaceInfo());
        m.put("playTime", e.getPlayTime());
        m.put("program", e.getProgram());
        m.put("spendTime", e.getSpendTime());
        m.put("organizer", e.getOrganizer());
        m.put("organizerTel", e.getOrganizerTel());
        m.put("supervisor", e.getSupervisor());
        m.put("supervisorTel", e.getSupervisorTel());
        m.put("subEvent", e.getSubEvent());
        m.put("feeInfo", e.getFeeInfo());

        return m;
    }

    private Map<String, Object> getLodgeInfo(int contentId) {
        var e = lodgeInformationRepository.findByContent_ContentId(contentId);

        if (e == null) return Map.of("type", "LODGE");

        var m = new HashMap<String, Object>();

        m.put("type", "LODGE");
        m.put("lodgeId", e.getLodgeId());
        m.put("capacityCount", e.getCapacityCount());
        m.put("goodstay", e.getGoodstay());
        m.put("benikia", e.getBenikia());
        m.put("checkInTime", e.getCheckInTime());
        m.put("checkOutTime", e.getCheckOutTime());
        m.put("cooking", e.getCooking());
        m.put("foodplace", e.getFoodplace());
        m.put("hanok", e.getHanok());
        m.put("information", e.getInformation());
        m.put("parking", e.getParking());
        m.put("pickupService", e.getPickupService());
        m.put("roomCount", e.getRoomCount());
        m.put("reservationInfo", e.getReservationInfo());
        m.put("reservationUrl", e.getReservationUrl());
        m.put("roomType", e.getRoomType());
        m.put("scale", e.getScale());
        m.put("subFacility", e.getSubFacility());
        m.put("barbecue", e.getBarbecue());
        m.put("beauty", e.getBeauty());
        m.put("beverage", e.getBeverage());
        m.put("bicycle", e.getBicycle());
        m.put("campfire", e.getCampfire());
        m.put("fitness", e.getFitness());
        m.put("karaoke", e.getKaraoke());
        m.put("publicBath", e.getPublicBath());
        m.put("publicPcRoom", e.getPublicPcRoom());
        m.put("sauna", e.getSauna());
        m.put("seminar", e.getSeminar());
        m.put("sports", e.getSports());
        m.put("refundRegulation", e.getRefundRegulation());

        return m;
    }

    private Map<String, Object> getRestaurantInfo(int contentId) {
        var e = restaurantInformationRepository.findByContent_ContentId(contentId);

        if (e == null) return Map.of("type", "RESTAURANT");

        var m = new HashMap<String, Object>();

        m.put("type", "RESTAURANT");
        m.put("restaurantId", e.getRestaurantId());
        m.put("chkCreditcard", e.getChkCreditcard());
        m.put("discountInfo", e.getDiscountInfo());
        m.put("signatureMenu", e.getSignatureMenu());
        m.put("information", e.getInformation());
        m.put("kidsFacility", e.getKidsFacility());
        m.put("openDate", e.getOpenDate());
        m.put("openTime", e.getOpenTime());
        m.put("takeout", e.getTakeout());
        m.put("parking", e.getParking());
        m.put("reservation", e.getReservation());
        m.put("restDate", e.getRestDate());
        m.put("scale", e.getScale());
        m.put("seat", e.getSeat());
        m.put("smoking", e.getSmoking());
        m.put("treatMenu", e.getTreatMenu());

        return m;
    }

    private Map<String, Object> getSightsInfo(int contentId) {
        var e = sightsInformationRepository.findByContent_ContentId(contentId);

        if (e == null) return Map.of("type", "SIGHTS");

        var m = new HashMap<String, Object>();

        m.put("type", "SIGHTS");
        m.put("sightsId", e.getSightsId());
        m.put("contentTypeId", e.getContentTypeId());
        m.put("accomCount", e.getAccomCount());
        m.put("chkCreditcard", e.getChkCreditcard());
        m.put("expAgeRange", e.getExpAgeRange());
        m.put("expGuide", e.getExpGuide());
        m.put("infoCenter", e.getInfoCenter());
        m.put("openDate", e.getOpenDate());
        m.put("parking", e.getParking());
        m.put("restDate", e.getRestDate());
        m.put("useSeason", e.getUseSeason());
        m.put("useTime", e.getUseTime());
        m.put("heritage1", e.getHeritage1());
        m.put("heritage2", e.getHeritage2());
        m.put("heritage3", e.getHeritage3());

        return m;
    }

}
