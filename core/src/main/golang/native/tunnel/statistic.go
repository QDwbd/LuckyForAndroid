package tunnel

import (
	"sort"

	"github.com/metacubex/mihomo/tunnel/statistic"
)

func ResetStatistic() {
	statistic.DefaultManager.ResetStatistic()
}

func Now() (up int64, down int64) {
	return statistic.DefaultManager.Now()
}

func Total() (up int64, down int64) {
	return statistic.DefaultManager.Total()
}

// ConnectionTraffic 单个目标地址当前累计的上传/下载字节数
type ConnectionTraffic struct {
	Address  string `json:"address"`
	Upload   int64  `json:"upload"`
	Download int64  `json:"download"`
}

// QueryConnections 聚合当前连接，并统计每个地址累计的上传/下载流量（字节）。
// 仅返回有流量的地址（上传和下载累计字节数不全为 0），供调用方（Android 端）对相邻
// 两次结果做差值，换算成实时网速。
func QueryConnections() []ConnectionTraffic {
	byAddr := make(map[string]*ConnectionTraffic)

	snapshot := statistic.DefaultManager.Snapshot()
	for _, info := range snapshot.Connections {
		if info == nil || info.Metadata == nil {
			continue
		}
		address := info.Metadata.String()
		if address == "" {
			continue
		}
		item := byAddr[address]
		if item == nil {
			item = &ConnectionTraffic{Address: address}
			byAddr[address] = item
		}
		item.Upload += info.UploadTotal.Load()
		item.Download += info.DownloadTotal.Load()
	}

	result := make([]ConnectionTraffic, 0, len(byAddr))
	for _, v := range byAddr {
		// 上传和下载累计流量都为 0 的地址不展示
		if v.Upload == 0 && v.Download == 0 {
			continue
		}
		result = append(result, *v)
	}
	sort.Slice(result, func(i, j int) bool {
		return result[i].Address < result[j].Address
	})

	return result
}
