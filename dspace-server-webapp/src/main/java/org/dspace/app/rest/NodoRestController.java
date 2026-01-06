package org.dspace.app.rest;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.parameter.SearchFilter;
import org.dspace.app.rest.repository.NodoRepository;
import org.dspace.app.rest.repository.DiscoveryRestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.dspace.app.rest.utils.Utils;
import java.time.Year;

import java.util.*;

@RestController
@RequestMapping("/api/nodo")

public class NodoRestController {

    private static final Logger log = LogManager.getLogger();

    @Autowired
    private NodoRepository nodoRepository;

    @Autowired
    private DiscoveryRestRepository discoveryRestRepository;

    @Autowired
    protected Utils utils;

    @RequestMapping(method = RequestMethod.GET, path = "/production-yield")
    public Map<String, Integer> getYearlyProduction(@RequestParam(name = "startYear") Integer startYear,
                                                    @RequestParam(name = "endYear", required = false) Integer endYear) {

        if (endYear == null) {
            endYear = Year.now().getValue();
        }

        if (log.isTraceEnabled()) {
            log.trace("Searching products with query startYear:{} endYear:{}", startYear, endYear);
        }

        List<SearchFilter> searchFilters = new ArrayList<>();

        return nodoRepository.yearlyProduction(startYear, endYear, searchFilters, null);
    }
}