package org.pra.nse.db.dao;

import org.pra.nse.config.YamlPropertyLoaderFactory;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.db.dto.PivotOiDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PropertySource(value = "classpath:reports-query.yaml", factory = YamlPropertyLoaderFactory.class)
public class NseReportsDao {
    private final JdbcTemplate jdbcTemplate;

    @Value("${sqlDeliverySpike}")
    private String sqlPivotOi;

    NseReportsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DeliverySpikeDto> getDeliverySpike() {
        List<DeliverySpikeDto> result = jdbcTemplate.query(sqlPivotOi, new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class));
        return result;
    }
}
