import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


class Thing{
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Thing(int id, int price) {
        this.id = id;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Thing{" +
                "id=" + id +
                ", price=" + price +
                '}';
    }

    private int id;
    private int price;
}
public class Lab2b {
    private static final BlockingQueue<Thing> buffer1;
    private static final BlockingQueue<Thing> buffer2;
    private static List<Thing> things;
    static {
        buffer1 = new LinkedBlockingDeque<>();
        buffer2 = new LinkedBlockingDeque<>();
    }
    public static void main(String[] args) {
        things = IntStream.range(0,100).boxed()
                .map(i-> new Thing(i, (int) (Math.random() * 100)))
                .collect(Collectors.toList());
        int totalThingAtTheStart = things.stream().mapToInt(Thing::getPrice).sum();
        AtomicInteger totalPrice = new AtomicInteger(0);
        Thread ivanov = new Thread(()->{
            while (things.size()>0){
                int randomNumber = (int) (Math.random() * things.size());
                try {
                    buffer1.put(things.remove(randomNumber));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        Thread petrov = new Thread(()->{
            while (ivanov.isAlive() || !buffer1.isEmpty()){
                try {
                    buffer2.put(buffer1.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread nechyporchuk = new Thread(()->{
            while (petrov.isAlive() || buffer2.size()>0){
                try {
                    totalPrice.addAndGet(buffer2.take().getPrice());
                    System.out.println(totalPrice.addAndGet(buffer2.take().getPrice()) + " " + buffer2.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ivanov.start();
        petrov.start();
        nechyporchuk.start();
        while (nechyporchuk.isAlive()){}
        System.out.println("Start value of things: " + totalThingAtTheStart);
        System.out.println("Valuer at the end: " + totalPrice);
    }
}
