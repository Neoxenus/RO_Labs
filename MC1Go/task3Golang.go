package main

import (
	"fmt"
	"golang.org/x/sync/semaphore"
	"math/rand"
	"time"
)

var sem *semaphore.Weighted //=

type Car struct {
	id int
}

func NewClient(id int) *Car {
	return &Car{id: id}
}

func (car Car) call() {
	fmt.Println("Автомобіль ", car.id, " підїхав до парковки.")
	start := time.Now()
	for !sem.TryAcquire(int64(1)) {
		if time.Now().Sub(start) > time.Duration(rand.Intn(10))*time.Second+time.Second*3 {
			fmt.Println("Автомобіль ", car.id, " втомився чекати і залишив паркування.")
			return
		}
	}
	fmt.Println("Автомобіль ", car.id, " припаркувався на місці.")
	time.Sleep(time.Second * 5)
	fmt.Println("Автомобіль ", car.id, " залишив парковку.")
	sem.Release(int64(1))
}

func main() {
	sem = semaphore.NewWeighted(int64(5))
	rand.Seed(time.Now().Unix())
	for i := 1; i <= 10; i++ {
		go NewClient(i).call()
		time.Sleep(400 * time.Millisecond)
	}
	for {
	}
}
