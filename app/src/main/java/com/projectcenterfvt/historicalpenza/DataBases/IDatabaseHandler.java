package com.projectcenterfvt.historicalpenza.DataBases;

import java.util.ArrayList;

/**
 * Created by roman on 09.07.2018.
 */

public interface IDatabaseHandler {

    Sight getSight(int id);

    void addSight(Sight sight);

    void changeStatus(int id);

    void deleteAll();

    ArrayList<Sight> getAllSight();

}
