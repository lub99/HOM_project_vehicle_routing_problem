import matplotlib.pyplot as plt


time = ['1m', '5m', 'un']
vehicles1 = [11, 11, 11]
vehicles2 = [19, 19, 19]
vehicles3 = [37, 37, 37]
vehicles4 = [21, 20, 20]
vehicles5 = [79, 77, 79]
vehicles6 = [19, 19, 19]
distances1 = [1157.2485, 1182.0206, 1165.3051]
distances2 = [5880.9577, 5705.9605, 5755.1773]
distances3 = [9981.809, 9792.8223, 10079.973]
distances4 = [16347.3799, 13458.3913, 14127.7022]
distances5 = [28221.0049, 28956.214, 29740.8812]
distances6 = [79758.9033, 77445.1725, 73978.6278]

fig, axs = plt.subplots(3, 2)
fig.set_figheight(7)
fig.set_figwidth(7)

axs[0, 0].set_title('instance 1')
axs[0, 0].set_ylim([0, 15])
axs[0, 0].bar(time, vehicles1, color='blue', label='vehicles')
ax2 = axs[0, 0].twinx()
ax2.set_ylim([1100, 1200])
ax2.plot(time, distances1, color='red', linewidth=3, label='distance')

axs[0, 1].set_title('instance 2')
axs[0, 1].set_ylim([0, 25])
axs[0, 1].bar(time, vehicles2, color='blue', label='vehicles')
ax2 = axs[0, 1].twinx()
ax2.set_ylim([5000, 6500])
ax2.plot(time, distances2, color='red', linewidth=3, label='distance')

axs[1, 0].set_title('instance 3')
axs[1, 0].set_ylim([35, 40])
axs[1, 0].bar(time, vehicles3, color='blue', label='vehicles')
ax2 = axs[1, 0].twinx()
ax2.set_ylim([9000, 10500])
ax2.plot(time, distances3, color='red', linewidth=3, label='distance')

axs[1, 1].set_title('instance 4')
axs[1, 1].set_ylim([0, 25])
axs[1, 1].bar(time, vehicles4, color='blue', label='vehicles')
ax2 = axs[1, 1].twinx()
ax2.set_ylim([12000, 18000])
ax2.plot(time, distances4, color='red', linewidth=3, label='distance')

axs[2, 0].set_title('instance 5')
axs[2, 0].set_ylim([75, 80])
axs[2, 0].bar(time, vehicles5, color='blue', label='vehicles')
ax2 = axs[2, 0].twinx()
ax2.set_ylim([28000, 30000])
ax2.plot(time, distances5, color='red', linewidth=3, label='distance')

axs[2, 1].set_title('instance 6')
axs[2, 1].set_ylim([0, 25])
axs[2, 1].bar(time, vehicles6, color='blue', label='vehicles')
ax2 = axs[2, 1].twinx()
ax2.set_ylim([70000, 85000])
ax2.plot(time, distances6, color='red', linewidth=3, label='distance')

plt.legend()
plt.show()
