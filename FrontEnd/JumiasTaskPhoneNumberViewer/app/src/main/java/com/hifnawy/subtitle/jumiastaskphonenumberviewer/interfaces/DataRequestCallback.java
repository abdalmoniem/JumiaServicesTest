package com.hifnawy.subtitle.jumiastaskphonenumberviewer.interfaces;

/**
 * this interface is used to signal callbacks before, after and if an error occurs in a REST API
 * request
 *
 * @author AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since Jan 28th 2022
 */
public interface DataRequestCallback {

  /** method used as callback before a REST API is added to the Volley Request Queue */
  void dataPreLoaded();

  /** method used as callback if the response of a REST API is successful */
  void dataLoaded();

  /** method used as callback if the response of a REST API have failed */
  void dataLoadError();
}
