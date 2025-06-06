# Serenity

![ci.yml][link-ci]

OpenGL 3d terrain engine written in [Kotlin][link-kotlin] and [LWJGL][link-lwjgl].

Experiment results:

- Terrain generation and rendering (with dynamic LOD on the GPU)
- Ocean rendering (based on the Fast Fourier Transform on the GPU)
- Instanced model rendering (trees and grass)
- SkyDome rendering
- Post Processing effects (Light Scattering also known as God Rays)

## Requirements

- Kotlin 1.9
- JDK 17
- Maven
- OpenGL 4.6 compatible video card

## Project setup

- `maven build` - for building in IDE
- `maven package` - for packaging in executable binary

For now supports only windows-x64 natives

To build and compile for other platforms add lwjgl natives in maven build section

## Screenshots

![screen1](/src/main/resources/screenshots/screen1.png)
&nbsp;
![screen2](/src/main/resources/screenshots/screen2.png)

![screen3](/src/main/resources/screenshots/screen3.png)
&nbsp;
![screen4](/src/main/resources/screenshots/screen4.png)

## Demo

These input keys are used in the demo program:

- **W-A-S-D** - camera movement
- **Arrow Keys** - camera rotation
- **+-** - sun movement
- **O** - play/pause ambient sounds

[Check out windows binaries (128MB)][link-binaries]

## License

MIT. Please see the [license file](LICENSE.md) for more information.

## Inspiration and thankful links

- [Real Time 3d terrain engines using C++ and DirectX 9] (https://www.amazon.com/Real-Time-Terrain-Engines-DirectX9-Development/dp/1584502045)
- [Oreon Engine] (https://github.com/fynnfluegge/oreon-engine)
- [ETEngine] (https://github.com/Illation/ETEngine)
- [Sebastian Lague] (https://github.com/SebLague)
- [OGLdev] (https://ogldev.org/)
- [TM] (https://github.com/TheThinMatrix)
- [Ocean fft] (https://github.com/czartur/ocean_fft)
- [3d assets] (https://quaternius.com/)
- [Dear ImGui] (https://github.com/ocornut/imgui)

[link-kotlin]: https://kotlinlang.org/
[link-lwjgl]: https://www.lwjgl.org/
[link-ci]: https://github.com/shirokovnv/serenity/actions/workflows/maven.yml/badge.svg
[link-binaries]: https://drive.google.com/file/d/1ndBigRdF7PpoxJ3A5oRA-M8wIrzsg1A8/view?usp=drive_link