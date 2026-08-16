package features

func Tags() (tags []string) {
	if LQWBD {
		tags = append(tags, "lqwbd")
	}
	if WithLowMemory {
		tags = append(tags, "with_low_memory")
	}
	if NoFakeTCP {
		tags = append(tags, "no_fake_tcp")
	}
	if WithGVisor {
		tags = append(tags, "with_gvisor")
	}
	return
}
