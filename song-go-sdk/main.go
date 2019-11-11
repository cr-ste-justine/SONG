package main

import (
	"fmt"
	"os"
	"song-go-sdk/client"
	"song-go-sdk/study"
)

func main() {
	cli := client.Client{
		ServerUrl: "http://localhost:8080",
		Token:     "ad83ebde-a55c-11e7-abc4-cec278b6b50a",
	}
	studyIds, err := study.GetAll(&cli)
	if err != nil {
		os.Exit(1)
	}
	fmt.Printf("%v", studyIds)
}
