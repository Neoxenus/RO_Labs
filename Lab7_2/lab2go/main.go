package main

import (
	"fmt"
	"math/rand"
	"time"
)

type Monk struct {
	energy int
	temple bool
}

func NewMonk(energy int, temple bool) *Monk {
	return &Monk{energy: energy, temple: temple}
}

func battle(a Monk, b Monk, c chan Monk) {
	if a.energy > b.energy {
		c <- a
	} else {
		c <- b
	}
}

func main() {
	s1 := rand.NewSource(time.Now().UnixNano())
	r1 := rand.New(s1)
	var array []Monk
	size := 16
	for i := 0; i < size; i++ {
		m := NewMonk(r1.Intn(100), i%2 == 0)
		array = append(array, *m)
	}
	fmt.Println(array)

	c := make(chan Monk)
	for size > 1 {
		for i := 0; i < size; i += 2 {
			go battle(array[i], array[i+1], c)
		}
		size /= 2
		array = nil
		for i := 0; i < size; i++ {
			array = append(array, <-c)
		}

	}
	fmt.Println(array)
	if array[0].temple {
		fmt.Print("Переміг Гуань-Інь")
	} else {
		fmt.Print("Переміг Гуань-Янь")
	}
	fmt.Println(" з силою:", array[0].energy)
}
