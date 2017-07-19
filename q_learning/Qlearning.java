public class Qlearning{
  public static void main(String[] args){
    /* 環境の生成 */
    Environment env = new Environment();
    /* エージェントの生成 */
    Agent agent = new Agent(env);
    /* エージェントの学習 */
    agent.learn();
    /* Q値を出力 */
    agent.print_Q();
    /* エージェントのテスト */
    agent.test();
  }
}    
