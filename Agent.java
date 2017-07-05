public class Agent{
  /* 環境 */
  private Environment env;
  /* 割引率 */
  private static final double GAMMA = 0.9;
  /* 学習率 */
  private static final double ALPHA = 0.1;
  /* 探索回数 */
  private static final int TMAX = 1000000;
  /* Q値 */
  private double[][] Q = {{0.0, 0.0},
                          {0.0, 0.0},
                          {0.0, 0.0},
                          {0.0, 0.0}};

  /* コンストラクタ */
  public Agent(Environment env){
    /* 環境の設定 */
    this.env = env;
  }

  /* 学習 */
  public void learn(){
    /* 現在の状態 */
    int state = 0;
    /* TMAX回行動する */
    for(int t = 0; t < TMAX; t++){
      /* 行動の選択 */
      int action = select_action();
      /* 次の状態の観測 */
      int next_state = env.observe_state(state, action);
      /* 報酬の観測 */
      int reward = env.observe_reward(state, action);
      /* 次の状態におけるQ値の最大値を求める */
      double next_Q_max = max(Q[next_state]);
      /* Q値の更新 */
      Q[state][action] = 
        (1 -ALPHA) * Q[state][action] + ALPHA * (reward + GAMMA * next_Q_max);
      /* 次の状態へ遷移 */
      state = next_state;
    }
  }
  /* ランダム法による行動選択 */
  private int select_action(){
    /* 0か1を返す */
    return new java.util.Random().nextInt(2);
  }
  /* 学習したQ値を使って行動 */
  public void test(){
    /* 合計報酬 */
    int total_reward = 0;
    /* 現在の状態 */
    int state = 0;
    /* TMAX回行動する */
    for(int t = 0; t < TMAX; t++){
      /* 行動の選択 */
      int action = argmax(Q[state]);
      /* 次の状態の観測 */
      int next_state = env.observe_state(state, action);
      /* 報酬の観測 */
      int reward = env.observe_reward(state, action);
      /* 次の状態へ遷移 */
      state = next_state;
      /* 報酬の加算 */
      total_reward += reward;
    }
    /* 報酬の出力 */
    System.out.println("total reward");
    System.out.println(total_reward);
  }
  /* Q値の出力 */
  public void print_Q(){
    System.out.println("Q Value");
    for(int i = 0; i < Q.length; i++){
      for(int j = 0; j < Q[i].length; j++)
        System.out.print(Q[i][j]+" ");
      System.out.println();
    }
  }
  /* 最大値を求める */
  public double max(double[] array){
    return array[argmax(array)];
  }
  /* 最大値のインデックスを求める */
  public int argmax(double[] array){
    int max_index = 0;
    double max = array[0];
    for(int i = 0; i < array.length; i++)
      if(array[i] > max){
        max = array[i];
        max_index = i;
      }
    return max_index;
  }
}
