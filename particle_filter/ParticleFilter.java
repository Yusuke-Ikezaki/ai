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

  /* コンストラクタ */
  public ParticleFilter(int state_n, int particle_n, double observe_p, double action_p){
    this.state_n = state_n;
    this.particle_n = particle_n;
    this.observe_p = observe_p;
    this.action_p = action_p;
    particles = new double[state_n];
    observes = new double[state_n][state_n];
    init();
  }

  /* 初期化 */
  public void init(){
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
  public int run(int initial_state, int tMax, boolean monitor){
    double num = particle_n / state_n;
    for(int i = 0; i < state_n; i++)
      particles[i] = num;
    int s = initial_state;
    int o = observe(s);
    if(monitor) print_situation(s, 0);
    for(int t = 1; t <= tMax; t++){
      int a = choose_action(o);
      int move = act(s, a);
      s += move;
      o = observe(s);
      sampling(a);
      importance(s);
      resampling();
      if(monitor && t % 10 == 0) print_situation(s, t);
    }
    return s;
  }
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
  public int observe(int s){
    double left_rand = Math.random();
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
  public int choose_action(int o){
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
  public int act(int s, int a){
    if(a == LEFT){
      if(s == 1) return STAY;
      else{
        double rand = Math.random();
        if(rand < action_p) return LEFT;
        else return STAY;
      }
    } else if(a == RIGHT){
      if(s == state_n) return STAY;
      else{
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
  public void sampling(int a){
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
  public void importance(int s){
    for(int i = 0; i < state_n; i++)
      particles[i] *= observes[s - 1][i];
  }
  public void resampling(){
    double sum = 0.0;
    for(int i = 0; i < state_n; i++)
      sum += particles[i];
    for(int i = 0; i < state_n; i++)
      particles[i] *= particle_n / sum;
  }
  public void print_situation(int s, int t){
    System.out.println("t = " + t);
    System.out.println("State: s = " + s);
    System.out.print("Particles: [");
    for(int i = 0; i < state_n - 1; i++)
      System.out.printf("%.3f , ", particles[i]);
    System.out.printf("%.3f]\n", particles[state_n - 1]);
  }
}
