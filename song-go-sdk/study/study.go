package study

import (
	"encoding/json"
	"song-go-sdk/client"
)

const (
	getAllStudiesUrl = "/studies/all"
	createStudyUrl   = `/studies/{{.studyId}}/`
	getStudy         = `/studies/{{.studyId}}`
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

func Create(id string, name string, description string, organization string, cli *client.Client) string {

}
