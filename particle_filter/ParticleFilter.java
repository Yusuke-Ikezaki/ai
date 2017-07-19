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
  /* 粒子の分布 */
  private double[] particles;
  /* 観測確率 */
  private double[][] observe_p;

  /* コンストラクタ */
  public ParticleFilter(int state_n, int particle_n){
    this.state_n = state_n;
    this.particle_n = particle_n;
    particles = new double[state_n];
    observe_p = new double[state_n][state_n];
  }

  /* 初期化 */
  public void init(){
    double num = particle_n / state_n;
    for(int i = 0; i < state_n; i++)
      particles[i] = num;
    for(int i = 0; i < state_n; i++){
      if(i == 0){
        observe_p[i][0] = 0.81;
        for(int j = 1; j < state_n - 1; j++)
          observe_p[i][j] = 0.09;
        observe_p[i][state_n - 1] = 0.01;
      } else if(i > 0 && i < state_n - 1){
        observe_p[i][0] = 0.09;
        for(int j = 1; j < state_n - 1; j++)
          observe_p[i][j] = 0.81;
        observe_p[i][state_n - 1] = 0.09;
      } else if(i == state_n - 1){
        observe_p[i][0] = 0.01;
        for(int j = 1; j < state_n - 1; j++)
          observe_p[i][j] = 0.09;
        observe_p[i][state_n - 1] = 0.81;
      } else{
        System.out.println("state is between 1 and " + state_n + ", but got " + i);
        System.exit(1);
      }
    }
  }
  public int run(int initial_state, int tMax){
    int s = initial_state;
    int o = observe(s);
    for(int t = 1; t <= tMax; t++){
      int a = choose_action(o);
      int move = act(s, a);
      s += move;
      o = observe(s);
      sampling(a);
      double[] w = importance(s);
      resampling(w);
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
      if(left_rand < 0.9){
        if(right_rand < 0.9) return 3;
        else return 4;
      } else{
        if(right_rand < 0.9) return 1;
        else return 2;
      }
    } else if(s > 1 && s < particles.length){
      if(left_rand < 0.9){
        if(right_rand < 0.9) return 1;
        else return 2;
      } else{
        if(right_rand < 0.9) return 3;
        else return 4;
      }
    } else if(s == particles.length){
      if(left_rand < 0.9){
        if(right_rand < 0.9) return 2;
        else return 1;
      } else{
        if(right_rand < 0.9) return 4;
        else return 3;
      }
    } else{
      System.out.println("s is between 1 and " + particles.length + ", but got" + s);
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
        if(rand < 0.8) return LEFT;
        else return STAY;
      }
    } else if(a == RIGHT){
      if(s == 5) return STAY;
      else{
        double rand = Math.random();
        if(rand < 0.8) return RIGHT;
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
    double[] copy = new double[particles.length];
    for(int i = 0; i < copy.length; i++)
      copy[i] = 0.0;
    if(a == LEFT){
      copy[0] += particles[0];
      for(int i = 1; i < copy.length; i++){
        copy[i - 1] += particles[i] * 0.8;
        copy[i] += particles[i] * 0.2;
      }
    } else if(a == RIGHT){
      for(int i = 0; i < copy.length - 1; i++){
        copy[i + 1] += particles[i] * 0.8;
        copy[i] += particles[i] * 0.2;
      }
      copy[copy.length - 1] += particles[particles.length - 1];
    } else if(a == STAY){
      copy = particles;
    } else{
      System.out.println("a is between -1 and 1, but got " + a);
      System.exit(1);
    }
    particles = copy;
  }
  public double[] importance(int s){
    double[] w = new double[particles.length];
    for(int i = 0; i < w.length; i++)
      w[i] = particles[i] * observe_p[s - 1][i];
    return w;
  }
  public void resampling(double[] w){
    double sum = 0.0;
    for(int i = 0; i < w.length; i++)
      sum += w[i];
    for(int i = 0; i < particles.length; i++)
      particles[i] = particle_n * w[i] / sum;
  }
  public void print_situation(int s, int t){
    System.out.println("t = " + t);
    System.out.println("Wheel Duck: s = " + s);
    System.out.print("Particles: [");
    for(int i = 0; i < particles.length - 1; i++)
      System.out.printf("%.3f , ", particles[i]);
    System.out.printf("%.3f]\n", particles[particles.length - 1]);
  }
}