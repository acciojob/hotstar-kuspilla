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
        subscription.setSubscriptionType( subscriptionEntryDto.getSubscriptionType());
        Integer numberOfScreen = subscriptionEntryDto.getNoOfScreensRequired();
        Integer userId = subscriptionEntryDto.getUserId();
        Integer paidAmount = 0;

        User user = userRepository.findById(userId).get();

        if( user == null) return null;

        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());

        if( subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            paidAmount = 500 + 200 * numberOfScreen;
        }
        else if( subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            paidAmount = 1000 + 350 * numberOfScreen;

        }
        else if( subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            paidAmount = 800 + 250 * numberOfScreen;

        }

        subscription.setStartSubscriptionDate(new Date());
        subscription.setTotalAmountPaid(paidAmount);
        user.setSubscription(subscription);
        subscription.setUser(user);
        userRepository.save(user);
        subscriptionRepository.save(subscription);

        return paidAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
       User user = userRepository.findById(userId).get();
       if( user == null) throw new Exception("User Not found");

       Subscription subscription = user.getSubscription();
        if( subscription.equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }

       Integer paidAmount = subscription.getTotalAmountPaid();
       paidAmount = 1000 + 350 * subscription.getNoOfScreensSubscribed() - paidAmount;
       subscription.setSubscriptionType(SubscriptionType.ELITE);
       subscription.setTotalAmountPaid(paidAmount);
       user.setSubscription(subscription);
       userRepository.save(user);
       subscriptionRepository.save(subscription);

        return paidAmount;
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
