package org.dspace.app.rest.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.dspace.app.rest.parameter.SearchFilter;
import org.dspace.app.rest.utils.RestDiscoverQueryBuilder;
import org.dspace.core.Context;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.SearchService;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.configuration.DiscoveryConfiguration;
import org.dspace.discovery.configuration.DiscoveryConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Component
public class NodoRepository extends AbstractDSpaceRestRepository {
    private static final Logger log = LogManager.getLogger();

    @Autowired
    private RestDiscoverQueryBuilder queryBuilder;

    @Autowired
    private DiscoveryConfigurationService searchConfigurationService;

    @Autowired
    private SearchService searchService;


    public Map<String, Integer> yearlyProduction(int startYear, int endYear, List<SearchFilter> searchFilters, String dsoTypes) {

        Context context = obtainContext();
        String configuration = "researchoutputs";

        DiscoveryConfiguration discoveryConfiguration = searchConfigurationService
                .getDiscoveryConfigurationByNameOrIndexableObject(context, configuration, null);

        DiscoverQuery discoverQuery;
        List<SolrDocument> searchResult;
        String query = "dc.date.issued_dt:[" + startYear + "-01-01T00:00:00Z TO " + endYear + "-12-31T23:59:59Z] " +
                "AND entityType_keyword:Publication";

        // Extra fields to include in the search result
        List<String> fieldList = List.of(
                "search.resourceid",
                "search.resourcetype",
                "entityType_keyword",
                "dc.date.issued",
                "dc.type"
        );

        try {
            discoverQuery = queryBuilder
                    .buildQuery(context, null, discoveryConfiguration, query, searchFilters, dsoTypes, null);
            searchResult = searchService.rawSearch(context, discoverQuery, fieldList);

        } catch (
                SearchServiceException e) {
            log.error("Error while searching solrQuery", e);
            throw new IllegalArgumentException("Error while searching solrQuery " + e.getMessage());
        }

        // Init yearlyCount map to count products per year
        Map<String, Integer> yearlyCount = new HashMap<>();
        int[] yearArray = IntStream.iterate(startYear, i -> i <= endYear, i -> i + 1)
                .toArray();
        for (int year : yearArray) {
            yearlyCount.put(String.valueOf(year), 0);
        }

        for (SolrDocument doc : searchResult) {

            Object issuedDateObj = doc.getFieldValue("dc.date.issued");
            List<Object> issuedDateList = issuedDateObj instanceof List ? (List<Object>) issuedDateObj : Collections.emptyList();
            if (!issuedDateList.isEmpty()) {
                String issuedDate = (String) issuedDateList.get(0);
                String year = issuedDate.substring(0, 4);
                yearlyCount.put(year, yearlyCount.get(year) + 1);
            }
        }


        return yearlyCount;
    }
}