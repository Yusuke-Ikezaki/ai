public class Test{
  /* テスト回数 */
  private static final int TESTNUM = 1;

  public static void main(String[] args){
    /* 粒子フィルタの生成 */
    ParticleFilter p = new ParticleFilter(5, 10, 0.9, 0.8);
    p.setMonitorInterval(10);
    /* 正解数 */
    int cnt = 0;
    for(int i = 0; i < TESTNUM; i++){
      /* 行動させて最終的な状態を得る */
      int result = p.run(3, 100, true);
      /* 予測と結果が一致すれば正解 */
      if(p.predict() == result) cnt++;
    }
    /* 正解率の出力 */
    System.out.println("Predict Accuracy = " + ((double)cnt / TESTNUM));
  }
}
