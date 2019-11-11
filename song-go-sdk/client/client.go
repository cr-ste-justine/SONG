package client

import (
	"fmt"
	"io/ioutil"
	"net/http"
)

type Client struct {
	ServerUrl string
	Token     string
}

func (cli *Client) Get(path string, expected_code int) (*[]byte, error) {
	client := &http.Client{}
	req, err := http.NewRequest("GET", cli.ServerUrl+path, nil)
	if err != nil {
		return nil, err
	}
	req.Header.Set("Authorization", "bearer "+cli.Token)

	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if resp.StatusCode != 200 {
		return nil, fmt.Errorf("HTTP Response Error %d\n", resp.StatusCode)
	}
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}
	return &body, err
}
