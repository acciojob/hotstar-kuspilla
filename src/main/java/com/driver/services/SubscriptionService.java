package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.function.SupplierUtils;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        Integer userId = subscriptionEntryDto.getUserId();
        User user = userRepository.findById(userId).get();

        subscription.setSubscriptionType( subscriptionEntryDto.getSubscriptionType());
        Integer numberOfScreen = subscriptionEntryDto.getNoOfScreensRequired();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());

        Integer paidAmount = 0;
        if( subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            paidAmount = 500 + (200 * numberOfScreen);
        }
        else if( subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            paidAmount = 1000 + ( 350 * numberOfScreen);

        }
        else if( subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            paidAmount = 800 + ( 250 * numberOfScreen );

        }

        subscription.setTotalAmountPaid(paidAmount);
        user.setSubscription(subscription);
        subscription.setUser(user);
        Date date = new Date();
        subscription.setStartSubscriptionDate(date);
        user.setSubscription(subscription);

        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
       User user = userRepository.findById(userId).get();

       Subscription subscription = user.getSubscription();
        if( subscription.toString().equals("ELITE") ){
            throw new Exception("Already the best Subscription");
        }

       Integer lastPayment = subscription.getTotalAmountPaid();
        Integer paidAmount;
        if( subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            paidAmount = lastPayment + 300 + (50 * subscription.getNoOfScreensSubscribed() );

        }
        else {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            paidAmount = lastPayment + 200 + (100 * subscription.getNoOfScreensSubscribed() );
        }

       subscription.setTotalAmountPaid(paidAmount);
       user.setSubscription(subscription);
       subscriptionRepository.save(subscription);

        return paidAmount - lastPayment;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
       Integer revenue = 0;
       List<Subscription> subscriptionList = subscriptionRepository.findAll();
       for( Subscription temp : subscriptionList){
           revenue += temp.getTotalAmountPaid();
       }

        return revenue;
    }

}
