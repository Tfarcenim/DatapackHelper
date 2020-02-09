package com.tfar.datapackhelper.network;

import java.io.File;

public class FileFactory {

  private static int index = 1;

  public static File getNext(String recipe){
    File file = new File(recipe +"/"+index+".json");
    while (file.exists()){
      index++;
      file = new File(recipe +"/"+index+".json");
    }
    return file;
  }
}
