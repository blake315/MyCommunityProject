package com.zeeway.community.dao.impl;

import com.zeeway.community.dao.AlphaDao;
import org.springframework.stereotype.Repository;

@Repository("alphaFirst")
public class AlphaDaoImpl implements AlphaDao {
    @Override
    public String select() {
        return "Alpha Dao";
    }
}
