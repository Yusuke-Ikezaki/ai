package ai_report;

public class Environment{
  /* 報酬 */
  private int[][] r = {{-10, 2},
                       {-10, 2},
                       {-10, 2},
                       {100, 2}};
  /* 状態遷移が成功する確率 */
  private double[][] p = {{0.8, 1.0},
                          {0.5, 1.0},
                          {0.8, 1.0},
                          {1.0, 1.0}};
  /* 状態遷移が成功したときの遷移先 */
  private int[][] t = {{1, 0},
                       {2, 0},
                       {3, 1},
                       {3, 2}};

  /* 報酬の観測 */
  public int observe_reward(int state, int action){
    return r[state][action];
  }
  /* 状態の観測 */
  public int observe_state(int state, int action){
    double rand = Math.random();
    /* 状態遷移が成功 */
    if(rand < p[state][action]) return t[state][action];
    /* 状態遷移が失敗 */
    else return state;
  }
}