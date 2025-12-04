import React, { useEffect, useState } from 'react';
import { SectionTitle } from './ui/SectionTitle';
import { EventCard } from './EventCard';
import { dailyEvents } from '../data';

interface ServerStatus {
    day: number;
    permadeath: boolean;
    active_modifiers: string[];
    daily_roll_odds?: {
        common: number;
        rare: number;
        epic: number;
        legendary: number;
    };
    players: {
        name: string;
        uuid: string;
        online: boolean;
        lives: string | number;
        team?: string;
        role?: string;
    }[];
}

export const ActiveModifiers: React.FC = () => {
    const [status, setStatus] = useState<ServerStatus | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchStatus = async () => {
            try {
                const response = await fetch('https://thingproxy-760k.onrender.com/fetch/http://rollanddeath.play.hosting:33503/status');
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

    // Filter events based on the fetched list
    const activeEvents = dailyEvents.filter(event => 
        status?.active_modifiers.includes(event.name)
    );

    return (
        <div className="animate-in fade-in duration-500 space-y-12">
            {/* Server Info Header */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-zinc-900/80 p-6 border-l-4 border-red-600">
                    <h3 className="text-gray-400 uppercase text-sm font-bold mb-1">Día Actual</h3>
                    <p className="text-4xl text-white font-mono">{status?.day}</p>
                </div>
                <div className="bg-zinc-900/80 p-6 border-l-4 border-purple-600">
                    <h3 className="text-gray-400 uppercase text-sm font-bold mb-1">Modificadores</h3>
                    <p className="text-4xl text-white font-mono">{status?.active_modifiers.length}</p>
                </div>
                <div className="bg-zinc-900/80 p-6 border-l-4 border-blue-600">
                    <h3 className="text-gray-400 uppercase text-sm font-bold mb-1">Jugadores Online</h3>
                    <p className="text-4xl text-white font-mono">
                        {status?.players.filter(p => p.online).length} <span className="text-lg text-gray-500">/ {status?.players.length}</span>
                    </p>
                </div>
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
                    {status?.players.map((player) => (
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
                                    <span className="bg-red-900/30 text-red-300 px-1.5 py-0.5 rounded">❤️ {player.lives}</span>
                                    {player.team && <span className="bg-blue-900/30 text-blue-300 px-1.5 py-0.5 rounded">🛡️ {player.team}</span>}
                                    {player.role && <span className="bg-purple-900/30 text-purple-300 px-1.5 py-0.5 rounded">🎭 {player.role}</span>}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};
