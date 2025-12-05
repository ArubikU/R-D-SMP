import React, { useEffect, useMemo, useState } from 'react';
import { SectionTitle } from './ui/SectionTitle';
import { EventCard } from './EventCard';
import { dailyEvents } from '../data';

interface PlayerInfo {
    name: string;
    uuid: string;
    online: boolean;
    lives: string | number;
    lives_remaining?: number;
    health?: number;
    max_health?: number;
    team?: string;
    role?: string;
}

interface ServerStatus {
    day: number;
    permadeath: boolean;
    active_modifiers: string[];
    next_event_seconds?: number;
    next_event_time?: number;
    daily_roll_odds?: {
        common: number;
        rare: number;
        epic: number;
        legendary: number;
    };
    players: PlayerInfo[];
}

export const ActiveModifiers: React.FC = () => {
    const [status, setStatus] = useState<ServerStatus | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchStatus = async () => {
            try {
                const response = await fetch('http://151.245.32.130:25573');
                if (!response.ok) throw new Error('Error al conectar con el servidor');
                const data = await response.json();
                setStatus(data);
            } catch (err) {
                console.error(err);
                setError('No se pudo obtener el estado del servidor.');
            } finally {
                setLoading(false);
            }
        };

        fetchStatus();
        const interval = setInterval(fetchStatus, 30000); // Update every 30s
        return () => clearInterval(interval);
    }, []);

    if (loading) return <div className="text-center text-gray-400 py-12">Cargando estado del servidor...</div>;
    if (error) return <div className="text-center text-red-400 py-12">{error}</div>;
    if (!status) return <div className="text-center text-gray-500 py-12">Sin datos disponibles por el momento.</div>;

    const onlineCount = status.players.filter((p) => p.online).length;
    const totalPlayers = status.players.length;

    const formatCountdown = (seconds?: number) => {
        if (seconds === undefined || seconds < 0) return '---';
        const total = Math.floor(seconds);
        const hours = Math.floor(total / 3600);
        const minutes = Math.floor((total % 3600) / 60);
        const secs = total % 60;
        if (hours > 0) {
            return `${hours}h ${minutes.toString().padStart(2, '0')}m`;
        }
        return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    };

    const statCards = useMemo(() => ([
        {
            label: 'Día Actual',
            value: status.day,
            accent: 'border-red-600',
            subtitle: `${status.active_modifiers.length} modificadores activos`,
        },
        {
            label: 'Permadeath',
            value: status.permadeath ? 'Activo' : 'Inactivo',
            accent: status.permadeath ? 'border-amber-500' : 'border-zinc-700',
            subtitle: status.permadeath ? 'Sin segundas oportunidades' : 'Respawn habilitado',
        },
        {
            label: 'Siguiente Evento',
            value: formatCountdown(status.next_event_seconds),
            accent: 'border-emerald-600',
            subtitle: 'Actualizado cada 30s',
        },
        {
            label: 'Jugadores Online',
            value: `${onlineCount} / ${totalPlayers}`,
            accent: 'border-blue-600',
            subtitle: `${onlineCount} conectados ahora mismo`,
        },
    ]), [onlineCount, status.active_modifiers.length, status.day, status.next_event_seconds, status.permadeath, totalPlayers]);

    const formatHearts = (player: PlayerInfo) => {
        if (!player.online) {
            return 'Fuera de línea';
        }
        if (player.health === undefined || player.max_health === undefined) {
            return 'Sin datos';
        }
        const hearts = player.health / 2;
        const maxHearts = player.max_health / 2;
        return `${hearts.toFixed(1)} ❤ / ${maxHearts.toFixed(1)} ❤`;
    };

    const formatLivesRemaining = (player: PlayerInfo) => {
        if (typeof player.lives_remaining === 'number') {
            return player.lives_remaining;
        }
        if (typeof player.lives === 'number') {
            return player.lives;
        }
        return player.lives ?? '?';
    };

    // Filter events based on the fetched list
    const activeEvents = dailyEvents.filter(event => 
        status.active_modifiers.includes(event.name)
    );

    return (
        <div className="animate-in fade-in duration-500 space-y-12">
            {/* Server Info Header */}
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
                {statCards.map((card) => (
                    <div key={card.label} className={`bg-zinc-900/80 p-6 border-l-4 ${card.accent}`}>
                        <h3 className="text-gray-400 uppercase text-sm font-bold mb-1">{card.label}</h3>
                        <p className="text-3xl text-white font-mono">{card.value}</p>
                        {card.subtitle && <p className="text-xs text-gray-500 mt-2">{card.subtitle}</p>}
                    </div>
                ))}
            </div>

            {/* Active Modifiers */}
            <div>
                <SectionTitle>Modificadores Activos</SectionTitle>
                {activeEvents.length > 0 ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {activeEvents.map((ev, idx) => (
                            <EventCard key={idx} event={ev} />
                        ))}
                    </div>
                ) : (
                    <div className="text-center py-12 border-2 border-dashed border-gray-800 rounded-lg">
                        <span className="text-4xl block mb-2">💤</span>
                        <p className="text-gray-500">El servidor está tranquilo... por ahora.</p>
                    </div>
                )}
            </div>

            {/* Player List */}
            <div>
                <SectionTitle>Jugadores</SectionTitle>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {status.players.map((player) => (
                        <div key={player.uuid} className={`p-4 border ${player.online ? 'bg-zinc-900 border-green-900/50' : 'bg-zinc-950 border-zinc-800 opacity-70'} flex items-center gap-4`}>
                            <img 
                                src={`https://mc-heads.net/avatar/${player.name}/50`} 
                                alt={player.name} 
                                className={`w-12 h-12 rounded ${!player.online && 'grayscale'}`}
                            />
                            <div className="flex-1 min-w-0">
                                <div className="flex justify-between items-center">
                                    <h4 className={`font-bold truncate ${player.online ? 'text-white' : 'text-gray-500'}`}>{player.name}</h4>
                                    {player.online && <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>}
                                </div>
                                <div className="flex gap-2 text-xs mt-1">
                                    <span className="bg-red-900/30 text-red-300 px-1.5 py-0.5 rounded">❤️ {formatLivesRemaining(player)}</span>
                                    {player.team && <span className="bg-blue-900/30 text-blue-300 px-1.5 py-0.5 rounded">🛡️ {player.team}</span>}
                                    {player.role && <span className="bg-purple-900/30 text-purple-300 px-1.5 py-0.5 rounded">🎭 {player.role}</span>}
                                </div>
                                <div className="mt-2 text-xs text-gray-400 space-y-1">
                                    <p>Vida: {formatHearts(player)}</p>
                                    <p>Vidas restantes: {formatLivesRemaining(player)}</p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};
