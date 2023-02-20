package com.zeeway.community.dao.impl;

import com.zeeway.community.dao.AlphaDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphaMyBatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "this is a mybatis impl";
    }
}
