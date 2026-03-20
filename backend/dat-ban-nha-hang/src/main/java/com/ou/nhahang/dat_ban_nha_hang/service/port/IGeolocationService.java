package com.ou.nhahang.dat_ban_nha_hang.service.port;

import org.locationtech.jts.geom.Point;

public interface IGeolocationService {
    public Point getPointFromAddress(String address);
}
