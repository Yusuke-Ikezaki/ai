public class Test{
  public static void main(String[] args){
    ParticleFilter p = new ParticleFilter(5, 10);
    int cnt = 0;
    for(int i = 0; i < 1000; i++){
      p.init();
      int result = p.run(3, 100);
      if(p.predict() == result) cnt++;
    }
    System.out.println("Predict Correctness = " + (cnt / 1000.0));
  }
}