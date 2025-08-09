import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import { aliases, mdi } from 'vuetify/iconsets/mdi'

export default createVuetify(
    {
        components,
        theme: {
            defaultTheme: 'light',
            themes: {
                light: {
                    colors: {
                        primary: '#1976D2' // 기본 파란색
                    }
                }
            }
        },
        icons: {
            defaultSet: 'mdi',
            aliases,
            sets: { mdi },
        }
    },
)