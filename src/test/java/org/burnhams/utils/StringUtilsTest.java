package org.burnhams.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.burnhams.utils.StringUtils.twoSf;

public class StringUtilsTest {
    @Test
    public void testTwoSf() throws Exception {
        assertThat(twoSf(0.0001)).isEqualTo("0.0001");
        assertThat(twoSf(0.1234)).isEqualTo("0.12");
        assertThat(twoSf(43434.1234)).isEqualTo("43434.12");
    }
}
