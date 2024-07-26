package com.yp.puppy.api.util;

import com.yp.puppy.api.entity.user.Dog;
import org.springframework.stereotype.Service;


@Service
public class EnumTranslator {

    public static String translateDogSize(Dog.DogSize size) {
        switch (size) {
            case SMALL:
                return "소형견";
            case MEDIUM:
                return "중형견";
            case LARGE:
                return "대형견";
            default:
                return size.name(); // 기본적으로 영어 이름 반환
        }
    }

    public static String translateDogSex(Dog.Sex sex) {
        switch (sex) {
            case MALE:
                return "수컷";
            case FEMALE:
                return "암컷";
            default:
                return sex.name();
        }
    }


    public static String translateBreed(Dog.Breed breed) {
        switch (breed) {
            case RETRIEVER:
                return "리트리버";
            case SHEPHERD:
                return "셰퍼드";
            case BULLDOG:
                return "불독";
            case POODLE:
                return "푸들";
            case BEAGLE:
                return "비글";
            case YORKSHIRE_TERRIER:
                return "요크셔테리어";
            case DACHSHUND:
                return "닥스훈트";
            case WELSH_CORGI:
                return "웰시코기";
            case SIBERIAN_HUSKY:
                return "시베리안허스키";
            case DOBERMAN:
                return "도베르만";
            case SHIH_TZU:
                return "싯츄";
            case BOSTON_TERRIER:
                return "보스턴테리어";
            case POMERANIAN:
                return "포메라니언";
            default:
                return breed.name();
        }
    }



}
