package main

import (
	"fmt"
	"strconv"
	"sync"
)

var graph = [][]int{
	{0, 4, 0, 3},
	{4, 0, 6, 7},
	{0, 6, 0, 3},
	{3, 7, 3, 0},
}
var lock sync.RWMutex
var wg sync.WaitGroup

const INT_MAX = int(^uint(0) >> 1)

func changePrice(city1 int, city2 int, price int) {
	lock.Lock()
	if graph[city1][city2] != 0 {
		graph[city1][city2] = price
		graph[city2][city1] = price
	}
	lock.Unlock()
	fmt.Println("Price between city " + strconv.Itoa(city1) + " and " +
		strconv.Itoa(city2) + " changed to " + strconv.Itoa(price))
	graphPrint()
	defer wg.Done()
}

func addRoute(city1 int, city2 int, price int) {
	lock.Lock()
	if graph[city1][city2] == 0 {
		graph[city1][city2] = price
		graph[city2][city1] = price
	}
	lock.Unlock()
	fmt.Println("Route between " + strconv.Itoa(city1) + " and " +
		strconv.Itoa(city2) + " was added with price " + strconv.Itoa(price))
	graphPrint()
	defer wg.Done()

}
func deleteRoute(city1 int, city2 int) {
	lock.Lock()
	if graph[city1][city2] != 0 {
		graph[city1][city2] = 0
		graph[city2][city1] = 0
	}
	lock.Unlock()
	fmt.Println("Route between " + strconv.Itoa(city1) + " and " +
		strconv.Itoa(city2) + " was deleted")
	graphPrint()
	defer wg.Done()
}

func addNewCity() {
	lock.Lock()
	newSize := len(graph) + 1
	newGraph := make([][]int, newSize)
	for i := 0; i < newSize; i++ {
		newGraph[i] = make([]int, newSize)
	}
	for i := 0; i < newSize; i++ {
		for j := 0; j < newSize; j++ {
			if i != newSize-1 && j != newSize-1 {
				newGraph[i][j] = graph[i][j]
			} else {
				newGraph[i][j] = 0
			}
		}
	}
	graph = newGraph
	lock.Unlock()
	fmt.Println("Added new city " + strconv.Itoa(len(graph)-1))
	graphPrint()
	defer wg.Done()
}
func deleteCity(cityToDelete int) {
	lock.Lock()
	newSize := len(graph) - 1
	newGraph := make([][]int, newSize)
	for i := 0; i < newSize; i++ {
		newGraph[i] = make([]int, newSize)
	}
	ni := 0
	nj := 0
	for i := 0; i < len(graph); i++ {
		nj = 0
		for j := 0; j < len(graph); j++ {
			if j != cityToDelete {
				newGraph[ni][nj] = graph[i][j]
				nj++
			}
		}
		if i == cityToDelete {
			continue
		}
		ni++
	}
	graph = newGraph
	lock.Unlock()
	fmt.Println("Deleted city " + strconv.Itoa(cityToDelete))
	graphPrint()
	defer wg.Done()
}

func graphPrint() {
	lock.RLock()
	for i := 0; i < len(graph); i++ {
		for j := 0; j < len(graph); j++ {
			fmt.Print(graph[i][j], " ")
		}
		fmt.Print("\n")
	}
	fmt.Print("\n")
	lock.RUnlock()
}
func minDistance(dist []int, sptSet []bool) int {

	// Initialize min value
	min := INT_MAX
	var minIndex int

	for v := 0; v < len(graph); v++ {
		if sptSet[v] == false && dist[v] <= min {
			min = dist[v]
			minIndex = v
		}

	}

	return minIndex
}
func dijkstra(city1 int, city2 int) {
	lock.RLock()
	dist := make([]int, len(graph))

	sptSet := make([]bool, len(graph))
	for i := 0; i < len(graph); i++ {
		dist[i] = INT_MAX
		sptSet[i] = false
	}

	dist[city1] = 0

	for count := 0; count < len(graph)-1; count++ {
		u := minDistance(dist, sptSet)
		sptSet[u] = true
		for v := 0; v < len(graph); v++ {
			if !sptSet[v] && graph[u][v] != 0 && dist[u] != INT_MAX && dist[u]+graph[u][v] < dist[v] {
				dist[v] = dist[u] + graph[u][v]
			}
		}
	}

	fmt.Println("Shortest route from " + strconv.Itoa(city1) + " to " + strconv.Itoa(city2) + " cost " +
		strconv.Itoa(dist[city2]) + " grivnas")
	lock.RUnlock()
	graphPrint()
	defer wg.Done()
}
func main() {
	var choice int
	graphPrint()
	for {
		fmt.Println("1: Change price \n2: Add new route \n3: Delete existed route \n4: Add new city " +
			"\n5: Delete existed city \n6: Price between two cities")
		fmt.Scan(&choice)
		wg.Add(1)
		switch choice {
		case 1:
			fmt.Println("Enter first, second cities and new price:")
			var city1, city2, price int
			fmt.Scan(&city1, &city2, &price)
			go changePrice(city1, city2, price)

		case 2:
			fmt.Println("Enter first, second cities and price of new route:")
			var city1, city2, price int
			fmt.Scan(&city1, &city2, &price)
			go addRoute(city1, city2, price)

		case 3:
			fmt.Println("Enter first, second cities:")
			var city1, city2 int
			fmt.Scan(&city1, &city2)
			go deleteRoute(city1, city2)

		case 4:
			go addNewCity()
		case 5:
			fmt.Println("Enter city to delete:")
			var city1 int
			fmt.Scan(&city1)
			go deleteCity(city1)
		case 6:
			fmt.Println("Enter two cities: ")
			var city1, city2 int
			fmt.Scan(&city1, &city2)
			go dijkstra(city1, city2)
		default:
			fmt.Println("Invalid input")
			wg.Done()
		}
		wg.Wait()
	}
}
