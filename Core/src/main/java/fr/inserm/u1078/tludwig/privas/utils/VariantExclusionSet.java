package fr.inserm.u1078.tludwig.privas.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;

/**
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-12-13
 */
public class VariantExclusionSet {
  private final TreeSet<String> excluded;

  public VariantExclusionSet() {
    this.excluded = new TreeSet<>();
  }
  
  public VariantExclusionSet(String filename, String hashKey) throws IOException {
    this();
    if(filename != null && !filename.isEmpty())
      this.load(filename, hashKey);
  }
  
  public void load(String filename, String hashKey) throws IOException {
    UniversalReader in = new UniversalReader(filename);
    String line;
    while((line = in.readLine()) != null)
      if(!line.startsWith("#")){
        String[] f = line.split("\t", -1);
        this.add(Crypto.hashSHA256(hashKey, f[0]));
      }
    in.close();
  }
  
  public void add(String s){
    this.excluded.add(s);
  }
  
  public void add(VariantExclusionSet set){
    this.excluded.addAll(set.excluded);
  }
  
  public TreeSet<String> getSet(){
    return this.excluded;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean contains(String s){
    return this.excluded.contains(s);
  }
  
  public static VariantExclusionSet deserialize(String s) {
    VariantExclusionSet ves = new VariantExclusionSet();
    ves.excluded.addAll(Arrays.asList(s.split(",")));
    return ves;
  }
  
  public String serialize(){
    StringBuilder sb = new StringBuilder();
    for(String variant : this.excluded)
      sb.append(",").append(variant);
      
    if(sb.length() > 0)
      return sb.substring(1);
    return "";
  }
}
