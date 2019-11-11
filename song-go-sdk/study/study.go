package study

import (
	"encoding/json"
	"song-go-sdk/client"
)

const (
	getAllStudiesUrl = "/studies/all"
)

func GetAll(cli *client.Client) (*[]string, error) {
	var ids []string
	resp, err := cli.Get(getAllStudiesUrl, 200)
	if err != nil {
		return nil, err
	}
	err = json.Unmarshal(*resp, &ids)
	return &ids, err
}
