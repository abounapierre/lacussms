/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author SATELLITE
 */
public final class SmsScheduled extends TimerTask {

  /** Construct and use a TimerTask and Timer.
     * @param args */
  public static void main (String[] args) {
    TimerTask smsTask = new SmsScheduled();
    //perform the task once a day at 4 a.m., starting tomorrow morning
    //(other styles are possible as well)
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(smsTask, getTomorrowMorning4am(), fONCE_PER_DAY);
  }

  /**
  * Implements TimerTask's abstract run method.
  */
  @Override public void run(){
    System.out.println("Fetching mail...");
  }

  //expressed in milliseconds
  private final static long fONCE_PER_DAY = 1000*60*60*24;

  private final static int fONE_DAY = 1;
  private final static int fFOUR_AM = 4;
  private final static int fZERO_MINUTES = 0;

  private static Date getTomorrowMorning4am(){
    Calendar tomorrow = new GregorianCalendar();
    tomorrow.add(Calendar.DATE, fONE_DAY);
    Calendar result = new GregorianCalendar(
      tomorrow.get(Calendar.YEAR),
      tomorrow.get(Calendar.MONTH),
      tomorrow.get(Calendar.DATE),
      fFOUR_AM,
      fZERO_MINUTES
    );
    return result.getTime();
  }
}
