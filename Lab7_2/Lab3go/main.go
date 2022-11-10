package main

import (
	"fmt"
	"math/rand"
	"time"
)

// Enum//////////
const (
	Tobacco int = 0
	Paper       = 1
	Matches     = 2
)

func remove(slice []int, s int) []int {
	return append(slice[:s], slice[s+1:]...)
}

// Smoker /////////////////////////
type Smoker struct {
	name string
	comp int
}

func NewSmoker(name string, comp int) *Smoker {
	return &Smoker{name: name, comp: comp}
}

// Semaphore //////////////
type Semaphore struct {
	n chan int
}

func NewSemaphore() *Semaphore {
	return &Semaphore{n: make(chan int, 5)}
}

func (sem Semaphore) acquire() {
	sem.n <- 1
}

func (sem Semaphore) release() {
	<-sem.n
}
func (sem Semaphore) availablePermits() int {
	return 5 - len(sem.n)
}

// ///////////////////
func agent(semaphore Semaphore, r1 *rand.Rand, c chan []int) {
	for {
		if semaphore.availablePermits() == 1 && len(c) == 0 {
			smokeComps := remove([]int{Tobacco, Paper, Matches}, r1.Intn(3))
			fmt.Println(smokeComps)
			for i := 0; i < 3; i++ {
				c <- smokeComps
			}
			semaphore.acquire()
		}
	}
}

func (smoker Smoker) smoke(semaphore Semaphore, c chan []int) {
	for {
		if semaphore.availablePermits() == 0 {
			comps := <-c
			time.Sleep(1 * time.Millisecond)
			//fmt.Println(comps)
			isSmoking := true
			for i := 0; i < len(comps); i++ {
				if comps[i] == smoker.comp {
					isSmoking = false
				}
			}
			if isSmoking {
				fmt.Println(smoker.name + " is smoking")
				time.Sleep(1 * time.Second)
				semaphore.release()
			}
		}
	}
}

func main() {
	s1 := rand.NewSource(time.Now().UnixNano())
	r1 := rand.New(s1)
	c := make(chan []int, 3)
	semaphore := NewSemaphore()
	go agent(*semaphore, r1, c)
	go NewSmoker("Smoker-0", Tobacco).smoke(*semaphore, c)
	go NewSmoker("Smoker-1", Paper).smoke(*semaphore, c)
	go NewSmoker("Smoker-2", Matches).smoke(*semaphore, c)
	select {}
}
