public class Test{
  private static final int TESTNUM = 1;
  public static void main(String[] args){
    ParticleFilter p = new ParticleFilter(5, 10, 0.9, 0.8);
    int cnt = 0;
    for(int i = 0; i < TESTNUM; i++){
      p.init();
      int result = p.run(3, 100, true);
      if(p.predict() == result) cnt++;
    }
    System.out.println("Predict Accuracy = " + ((double)cnt / TESTNUM));
  }
}
