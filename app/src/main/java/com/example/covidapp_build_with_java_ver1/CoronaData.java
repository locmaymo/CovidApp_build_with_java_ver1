package com.example.covidapp_build_with_java_ver1;

import org.json.JSONException;
import org.json.JSONObject;
public class CoronaData {
    private String mRecovered,mCritical,mTodayDeath,mActive;
    private int mCase;
    public static CoronaData fromJson(JSONObject jsonObject)
    {
        try
        {
            CoronaData coronaData =new CoronaData();
            coronaData.mCritical=jsonObject.getString("critical");
        //    coronaData.mCondition=jsonObject.getInt("todayCases");
            coronaData.mTodayDeath=jsonObject.getString("deaths");
        //   coronaData.micon=updateCoronaIcon(coronaData.mCondition);
            coronaData.mRecovered = jsonObject.getString("recovered");
            coronaData.mActive = jsonObject.getString("active");
            return coronaData;
        }


        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
   // private static String  updateCoronaIcon(int mCase){}


    public String getRecoveredCase() {
        return mRecovered;
    }

    public String getmCriticalCase() {
        return mCritical ;
    }

    public String getDeathsCase() {
        return mTodayDeath ;
    }

    public  String getmActive(){ return mActive ; }
}

