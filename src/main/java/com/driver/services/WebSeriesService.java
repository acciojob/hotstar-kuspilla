package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName( webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

        WebSeries checkIsPresent = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if( checkIsPresent != null){
            throw new Exception("Series is already present");  // exception throw
        }

        Integer productionHouseId = webSeriesEntryDto.getProductionHouseId();
        ProductionHouse productionHouse = productionHouseRepository.findById(productionHouseId).get();
        if( productionHouse == null) throw new Exception("Production house is not present");

        webSeries.setProductionHouse(productionHouse);
        webSeries = webSeriesRepository.save(webSeries);
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        webSeriesList.add(webSeries);
        double productionHouseRating = 0;
        for( WebSeries temp : webSeriesList){
            productionHouseRating += temp.getRating();
        }
        productionHouseRating = productionHouseRating / webSeriesList.size();
        productionHouse.setRatings(productionHouseRating);
        productionHouseRepository.save(productionHouse);

        Integer id = webSeries.getId();

        return id;
    }

}
