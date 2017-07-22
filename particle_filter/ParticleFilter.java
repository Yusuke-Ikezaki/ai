public class ParticleFilter{
  /* 左 */
  private static final int LEFT = -1;
  /* 右 */
  private static final int RIGHT = 1;
  /* 停止 */
  private static final int STAY = 0;
  /* エラー */
  private static final int ERROR = -65536;
  /* 状態の数 */
  private int state_n;
  /* 粒子の数 */
  private int particle_n;
  /* 観測成功率 */
  private double observe_p;
  /* 行動成功率 */
  private double action_p;
  /* 粒子の分布 */
  private double[] particles;
  /* 観測確率 */
  private double[][] observes;
  /* モニター間隔 */
  private int monitor_interval = 1;

  /* コンストラクタ */
  /* ParticleFilter(状態数, 粒子数, 観測成功率, 行動成功率) */
  public ParticleFilter(int state_n, int particle_n, double observe_p, double action_p){
    this.state_n = state_n;
    this.particle_n = particle_n;
    this.observe_p = observe_p;
    this.action_p = action_p;
    particles = new double[state_n];
    observes = new double[state_n][state_n];
    init_observes();
  }

  /* 観測確率の初期化 */
  private void init_observes(){
    for(int i = 0; i < state_n; i++){
      if(i == 0){
        observes[i][0] = observe_p * observe_p;
        for(int j = 1; j < state_n - 1; j++)
          observes[i][j] = (1 - observe_p) * observe_p;
        observes[i][state_n - 1] = (1 - observe_p) * (1 - observe_p);
      } else if(i > 0 && i < state_n - 1){
        observes[i][0] = (1 - observe_p) * observe_p;
        for(int j = 1; j < state_n - 1; j++)
          observes[i][j] = observe_p * observe_p;
        observes[i][state_n - 1] = observe_p * (1 - observe_p);
      } else if(i == state_n - 1){
        observes[i][0] = (1 - observe_p) * (1 - observe_p);
        for(int j = 1; j < state_n - 1; j++)
          observes[i][j] = observe_p * (1 - observe_p);
        observes[i][state_n - 1] = observe_p * observe_p;
      } else{
        System.out.println("state is between 1 and " + state_n + ", but got " + i);
        System.exit(1);
      }
    }
  }
  /* 粒子の分布の初期化 */
  private void init_particles(){
    double num = particle_n / state_n;
    for(int i = 0; i < state_n; i++)
      particles[i] = num;
  }
  /* モニター間隔の設定 */
  public void setMonitorInterval(int monitor_interval){
      this.monitor_interval = monitor_interval;
  }
  /* 行動 */
  /* run(初期状態, 行動回数, モニターの有無) */
  public int run(int initial_state, int tMax, boolean monitor){
    /* 粒子の分布の初期化 */
    init_particles();
    /* 初期状態の設定 */
    int s = initial_state;
    /* 初期状態での観測 */
    int o = observe(s);
    /* 初期状況の出力 */
    if(monitor) print_situation(s, 0);
    for(int t = 1; t <= tMax; t++){
      /* 行動の選択 */
      int a = choose_action(o);
      /* 行動 */
      int move = act(s, a);
      s += move;
      /* 観測 */
      o = observe(s);
      /* サンプリング */
      sampling(a);
      /* インポータンス */
      importance(s);
      /* リサンプリング */
      resampling();
      /* monitor_intervalごとに状況を出力 */
      if(monitor && t % monitor_interval == 0) print_situation(s, t);
    }
    /* 最終的な状態を返す */
    return s;
  }
  /* 状態の予測 */
  public int predict(){
    double max = particles[0];
    int max_index = 0;
    for(int i = 1; i < state_n; i++)
      if(particles[i] > max){
        max = particles[i];
        max_index = i;
      }
    return max_index + 1;
  }
  /* 観測 */
  private int observe(int s){
    /* 左の壁の観測 */
    double left_rand = Math.random();
    /* 右の壁の観測 */
    double right_rand = Math.random();
    if(s == 1){
      if(left_rand < observe_p){
        if(right_rand < observe_p) return 3;
        else return 4;
      } else{
        if(right_rand < observe_p) return 1;
        else return 2;
      }
    } else if(s > 1 && s < state_n){
      if(left_rand < observe_p){
        if(right_rand < observe_p) return 1;
        else return 2;
      } else{
        if(right_rand < observe_p) return 3;
        else return 4;
      }
    } else if(s == state_n){
      if(left_rand < observe_p){
        if(right_rand < observe_p) return 2;
        else return 1;
      } else{
        if(right_rand < observe_p) return 4;
        else return 3;
      }
    } else{
      System.out.println("s is between 1 and " + state_n + ", but got" + s);
      System.exit(1);
      return ERROR;
    }
  }
  /* 行動の選択 */
  private int choose_action(int o){
    if(o == 1){
      double rand = Math.random();
      if(rand < 0.5) return LEFT;
      else return RIGHT;
    } else if(o == 2){
      return LEFT;
    } else if(o == 3){
      return RIGHT;
    } else if(o == 4){
      return STAY;
    } else{
      System.out.println("o is between 1 and 4, but got " + o);
      System.exit(1);
      return ERROR;
    }
  }
  /* 行動 */
  private int act(int s, int a){
    if(a == LEFT){
      if(s == 1) return STAY;
      else{
        /* 行動確率 */
        double rand = Math.random();
        if(rand < action_p) return LEFT;
        else return STAY;
      }
    } else if(a == RIGHT){
      if(s == state_n) return STAY;
      else{
        /* 行動確率 */
        double rand = Math.random();
        if(rand < action_p) return RIGHT;
        else return STAY;
      }
    } else if(a == STAY){
      return STAY;
    } else{
      System.out.println("a is between -1 and 1, but got " + a);
      System.exit(1);
      return ERROR;
    }
  }
  /* サンプリング */
  private void sampling(int a){
    double[] copy = new double[state_n];
    for(int i = 0; i < copy.length; i++)
      copy[i] = 0.0;
    if(a == LEFT){
      copy[0] += particles[0];
      for(int i = 1; i < copy.length; i++){
        copy[i - 1] += particles[i] * action_p;
        copy[i] += particles[i] * (1 - action_p);
      }
    } else if(a == RIGHT){
      for(int i = 0; i < copy.length - 1; i++){
        copy[i + 1] += particles[i] * action_p;
        copy[i] += particles[i] * (1 - action_p);
      }
      copy[copy.length - 1] += particles[state_n - 1];
    } else if(a == STAY){
      copy = particles;
    } else{
      System.out.println("a is between -1 and 1, but got " + a);
      System.exit(1);
    }
    particles = copy;
  }
  /* インポータンス */
  private void importance(int s){
    for(int i = 0; i < state_n; i++)
      particles[i] *= observes[s - 1][i];
  }
  /* リサンプリング */
  private void resampling(){
    double sum = 0.0;
    for(int i = 0; i < state_n; i++)
      sum += particles[i];
    for(int i = 0; i < state_n; i++)
      particles[i] *= particle_n / sum;
  }
  /* 状況の出力 */
  private void print_situation(int s, int t){
    System.out.println("t = " + t);
    System.out.println("State: s = " + s);
    System.out.print("Particles: [");
    for(int i = 0; i < state_n - 1; i++)
      System.out.printf("%.3f , ", particles[i]);
    System.out.printf("%.3f]\n", particles[state_n - 1]);
  }
}
